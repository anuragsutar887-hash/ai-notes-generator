package com.ainotes.data.repository

import android.net.Uri
import android.util.Log
import com.ainotes.BuildConfig
import com.ainotes.data.model.*
import com.ainotes.util.FileHelper
import com.ainotes.util.OcrHelper
import com.ainotes.util.PdfChunker
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class ProcessingProgress(
    val stage: String,          // "Extracting text", "Analyzing with AI", etc.
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val chunkIndex: Int = 0,
    val totalChunks: Int = 0
)

sealed class GeminiResult {
    data class Progress(val progress: ProcessingProgress) : GeminiResult()
    data class Success(val notes: StudyNotes) : GeminiResult()
    data class Error(val message: String) : GeminiResult()
}

@Singleton
class GeminiRepository @Inject constructor(
    private val pdfChunker: PdfChunker,
    private val ocrHelper: OcrHelper,
    private val fileHelper: FileHelper
) {
    companion object {
        private const val TAG = "GeminiRepository"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    private val gson = Gson()

    /**
     * Calls the Gemini REST API directly — bypasses the SDK's buggy gRPC serialization
     * that crashes on error responses (MissingFieldException for 'details' field).
     * Tries models in order; falls back to next on non-quota failure.
     */
    private suspend fun generateWithFallback(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        Log.d(TAG, "generateWithFallback: apiKey length is ${apiKey.length}")
        if (apiKey.isBlank() || apiKey == "YOUR_API_KEY") {
            throw IllegalArgumentException(
                "Gemini API Key is not configured. Please add GEMINI_API_KEY=your_key to local.properties and rebuild the app."
            )
        }

        val models = listOf(
            "gemini-2.5-flash",
            "gemini-3.5-flash",
            "gemini-3.1-flash-lite",
            "gemini-flash-latest",
            "gemini-2.0-flash",
            "gemini-2.5-pro"
        )

        val requestBodyJson = buildRequestJson(prompt)
        var lastError = "All models failed."

        for (modelName in models) {
            try {
                Log.d(TAG, "Attempting generation with model: $modelName")
                val url = java.net.URL("$BASE_URL/$modelName:generateContent?key=$apiKey")
                val connection = (url.openConnection() as java.net.HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                    connectTimeout = 30_000
                    readTimeout = 90_000
                }

                connection.outputStream.use { os ->
                    os.write(requestBodyJson.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                val responseText = if (responseCode == 200) {
                    connection.inputStream.bufferedReader(Charsets.UTF_8).readText()
                } else {
                    connection.errorStream?.bufferedReader(Charsets.UTF_8)?.readText() ?: ""
                }

                if (responseCode == 200) {
                    val text = parseGenerationResponse(responseText, modelName)
                    Log.d(TAG, "Success with model: $modelName, response length: ${text.length}")
                    return@withContext text
                } else {
                    val errorMsg = extractErrorMessage(responseText, responseCode)
                    Log.w(TAG, "Model $modelName returned HTTP $responseCode: $errorMsg")
                    // Log error and try next model
                    lastError = errorMsg
                }

            } catch (e: Exception) {
                Log.w(TAG, "Model $modelName threw exception: ${e.message}", e)
                lastError = e.message ?: "Unknown error"
            }
        }

        throw RuntimeException(lastError)
    }

    /** Build the REST request JSON body */
    private fun buildRequestJson(prompt: String): String {
        val escapedPrompt = gson.toJson(prompt) // safely escapes quotes and special chars
        return """
            {
                "contents": [
                    {
                        "parts": [
                            {"text": $escapedPrompt}
                        ]
                    }
                ],
                "generationConfig": {
                    "temperature": 0.3,
                    "maxOutputTokens": 8192
                }
            }
        """.trimIndent()
    }

    /** Parse the text from a successful Gemini REST response */
    private fun parseGenerationResponse(responseText: String, modelName: String): String {
        return try {
            val root = JsonParser.parseString(responseText).asJsonObject
            val candidates = root.getAsJsonArray("candidates")
            if (candidates != null && candidates.size() > 0) {
                val content = candidates[0].asJsonObject.getAsJsonObject("content")
                val parts = content?.getAsJsonArray("parts")
                if (parts != null && parts.size() > 0) {
                    parts[0].asJsonObject.get("text")?.asString ?: ""
                } else ""
            } else {
                // Check for promptFeedback (e.g. safety blocks)
                val feedback = root.getAsJsonObject("promptFeedback")
                val reason = feedback?.get("blockReason")?.asString
                if (reason != null) {
                    throw RuntimeException("Content blocked by safety filter: $reason")
                }
                throw RuntimeException("Empty response from model $modelName")
            }
        } catch (e: Exception) {
            if (e.message?.contains("blocked") == true || e.message?.contains("Empty response") == true) throw e
            Log.e(TAG, "Failed to parse response from $modelName: ${e.message}")
            throw RuntimeException("Failed to parse AI response: ${e.message}")
        }
    }

    /** Extract a human-readable error message from an error JSON response */
    private fun extractErrorMessage(errorBody: String, httpCode: Int): String {
        return try {
            val root = JsonParser.parseString(errorBody).asJsonObject
            val error = root.getAsJsonObject("error")
            error?.get("message")?.asString ?: "HTTP error $httpCode"
        } catch (e: Exception) {
            "HTTP error $httpCode"
        }
    }

    /**
     * Main entry point: processes a document and returns study notes as a Flow.
     * Emits progress updates then final result.
     */
    fun processDocument(
        uri: Uri,
        mode: GenerationMode,
        customQuery: String = "",
        inputType: InputType
    ): Flow<GeminiResult> = flow {

        try {
            // ── Step 1: Extract text ──────────────────────────────────────────
            emit(GeminiResult.Progress(ProcessingProgress("Extracting text from document…")))

            val textChunks = when (inputType) {
                InputType.PDF -> {
                    pdfChunker.extractChunks(uri) { _, _ -> }.also {
                        Log.d(TAG, "Extracted ${it.size} chunks from PDF")
                    }
                }
                InputType.IMAGE, InputType.HANDWRITTEN -> {
                    emit(GeminiResult.Progress(ProcessingProgress("Running OCR on image…")))
                    val text = ocrHelper.extractTextFromImage(uri)
                    listOf(text)
                }
                InputType.TEXT -> {
                    val text = fileHelper.readTextFile(uri)
                    listOf(text)
                }
            }

            if (textChunks.isEmpty() || textChunks.all { it.isBlank() }) {
                emit(GeminiResult.Error("Could not extract text from this file. Please ensure it contains readable content."))
                return@flow
            }

            // ── Step 2: Send each chunk to Gemini ─────────────────────────────
            val allFlashcards = mutableListOf<Flashcard>()
            val allKeyPoints = mutableListOf<KeyPoint>()
            val allFormulae = mutableListOf<Formula>()
            val allExamQuestions = mutableListOf<ExamQuestion>()
            val allCustomResults = StringBuilder()

            textChunks.forEachIndexed { index, chunk ->
                if (index > 0) {
                    kotlinx.coroutines.delay(1000)
                }
                emit(GeminiResult.Progress(
                    ProcessingProgress(
                        stage = "Analyzing document…",
                        chunkIndex = index + 1,
                        totalChunks = textChunks.size
                    )
                ))

                val prompt = buildPrompt(mode, customQuery, chunk, index + 1, textChunks.size)
                val responseText = generateWithFallback(prompt)

                Log.d(TAG, "Chunk ${index + 1} response length: ${responseText.length}")

                // Parse the JSON response for each mode
                when (mode) {
                    GenerationMode.FLASHCARDS -> allFlashcards.addAll(parseFlashcards(responseText))
                    GenerationMode.KEY_POINTS -> allKeyPoints.addAll(parseKeyPoints(responseText))
                    GenerationMode.FORMULAE -> allFormulae.addAll(parseFormulae(responseText))
                    GenerationMode.EXAM_QUESTIONS -> allExamQuestions.addAll(parseExamQuestions(responseText))
                    GenerationMode.CUSTOM -> allCustomResults.append(responseText).append("\n\n")
                }
            }

            // ── Step 3: Emit final result ─────────────────────────────────────
            val notes = StudyNotes(
                flashcards = allFlashcards,
                keyPoints = allKeyPoints,
                formulae = allFormulae,
                examQuestions = allExamQuestions,
                customResult = allCustomResults.toString().trim()
            )
            emit(GeminiResult.Success(notes))

        } catch (e: Exception) {
            Log.e(TAG, "Error processing document", e)
            emit(GeminiResult.Error(cleanErrorMessage(e)))
        }
    }

    /**
     * Process plain text directly (for copied/pasted content).
     */
    fun processText(
        text: String,
        mode: GenerationMode,
        customQuery: String = ""
    ): Flow<GeminiResult> = flow {
        try {
            emit(GeminiResult.Progress(ProcessingProgress("Analyzing document…", chunkIndex = 1, totalChunks = 1)))
            val prompt = buildPrompt(mode, customQuery, text, 1, 1)
            val responseText = generateWithFallback(prompt)

            val notes = when (mode) {
                GenerationMode.FLASHCARDS -> StudyNotes(flashcards = parseFlashcards(responseText))
                GenerationMode.KEY_POINTS -> StudyNotes(keyPoints = parseKeyPoints(responseText))
                GenerationMode.FORMULAE -> StudyNotes(formulae = parseFormulae(responseText))
                GenerationMode.EXAM_QUESTIONS -> StudyNotes(examQuestions = parseExamQuestions(responseText))
                GenerationMode.CUSTOM -> StudyNotes(customResult = responseText)
            }
            emit(GeminiResult.Success(notes))
        } catch (e: Exception) {
            emit(GeminiResult.Error(cleanErrorMessage(e)))
        }
    }

    private fun cleanErrorMessage(e: Exception): String {
        val msg = e.message ?: "Unknown error occurred"
        return when {
            msg.contains("Rate limit", ignoreCase = true) ->
                "Rate limit reached. Please wait a few seconds and try again."
            msg.contains("quota", ignoreCase = true) ||
            msg.contains("ResourceExhausted", ignoreCase = true) ||
            msg.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
            msg.contains("429") ->
                "Rate limit reached. Please wait a few seconds and try again."
            msg.contains("503") || msg.contains("UNAVAILABLE", ignoreCase = true) ||
            msg.contains("high demand", ignoreCase = true) ->
                "The AI service is temporarily unavailable due to high demand. Please try again in a moment."
            else -> msg
        }
    }

    // ─── Prompt Builders ──────────────────────────────────────────────────────

    private fun buildPrompt(
        mode: GenerationMode,
        customQuery: String,
        content: String,
        chunkIndex: Int,
        totalChunks: Int
    ): String {
        val chunkNote = if (totalChunks > 1)
            "\n[This is chunk $chunkIndex of $totalChunks. Extract as many items as possible from this portion.]\n"
        else ""

        return when (mode) {
            GenerationMode.FLASHCARDS -> """
                You are an expert study assistant. Analyze the following educational content and create comprehensive flashcards.
                $chunkNote
                Generate flashcards covering ALL important concepts, definitions, processes, dates, formulas, and facts.
                Aim for 10-15 high-quality flashcards for this content. Keep them clear, concise, and focused.
                
                RESPOND ONLY WITH VALID JSON in this exact format:
                {
                  "flashcards": [
                    {"front": "Question or term", "back": "Complete answer or definition", "topic": "Topic name"},
                    ...
                  ]
                }
                
                CONTENT:
                $content
            """.trimIndent()

            GenerationMode.KEY_POINTS -> """
                You are an expert study assistant. Analyze the following educational content and extract key points.
                $chunkNote
                Group key points by topic/chapter. Be thorough - include all important concepts, dates, processes, and facts.
                
                RESPOND ONLY WITH VALID JSON in this exact format:
                {
                  "keyPoints": [
                    {
                      "topic": "Topic/Chapter Name",
                      "points": ["Point 1", "Point 2", "Point 3", ...]
                    },
                    ...
                  ]
                }
                
                CONTENT:
                $content
            """.trimIndent()

            GenerationMode.FORMULAE -> """
                You are an expert study assistant. Extract ALL formulae, equations, mathematical expressions, and scientific laws from the content.
                $chunkNote
                
                RESPOND ONLY WITH VALID JSON in this exact format:
                {
                  "formulae": [
                    {
                      "name": "Formula/Law Name",
                      "formula": "The formula in plain text (e.g. F = ma)",
                      "explanation": "What each variable means and when to use this formula",
                      "topic": "Topic/Subject area"
                    },
                    ...
                  ]
                }
                
                If no formulae found, return: {"formulae": []}
                
                CONTENT:
                $content
            """.trimIndent()

            GenerationMode.EXAM_QUESTIONS -> """
                You are an expert exam question setter. Generate a comprehensive set of exam questions from the following content.
                $chunkNote
                Include a mix of:
                - Short answer questions (2-3 marks)
                - Long answer questions (5-10 marks)
                - Multiple choice questions (MCQ) with 4 options
                - True/False questions
                
                Focus on questions that are LIKELY TO APPEAR IN ACTUAL EXAMS.
                Aim for 5-8 high-quality questions representing the core concepts.
                
                RESPOND ONLY WITH VALID JSON in this exact format:
                {
                  "questions": [
                    {
                      "question": "Question text",
                      "answer": "Model answer",
                      "type": "SHORT_ANSWER",
                      "marks": 3,
                      "options": []
                    },
                    {
                      "question": "MCQ question text",
                      "answer": "Correct option text",
                      "type": "MCQ",
                      "marks": 1,
                      "options": ["Option A", "Option B", "Option C", "Option D"]
                    },
                    ...
                  ]
                }
                
                CONTENT:
                $content
            """.trimIndent()

            GenerationMode.CUSTOM -> """
                You are an expert study assistant. The user has the following request about the educational content below:
                
                USER REQUEST: $customQuery
                $chunkNote
                
                Please fulfill the user's request thoroughly and helpfully. Format your response in a clear, readable way.
                
                CONTENT:
                $content
            """.trimIndent()
        }
    }

    // ─── Response Parsers ─────────────────────────────────────────────────────

    private fun parseFlashcards(json: String): List<Flashcard> {
        return try {
            val cleanJson = extractJson(json)
            val root = JsonParser.parseString(cleanJson).asJsonObject
            val arr = root.getAsJsonArray("flashcards")
            arr.map { el ->
                val obj = el.asJsonObject
                Flashcard(
                    front = obj.get("front")?.asString ?: "",
                    back = obj.get("back")?.asString ?: "",
                    topic = obj.get("topic")?.asString ?: ""
                )
            }.filter { it.front.isNotBlank() && it.back.isNotBlank() }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing flashcards: ${e.message}")
            emptyList()
        }
    }

    private fun parseKeyPoints(json: String): List<KeyPoint> {
        return try {
            val cleanJson = extractJson(json)
            val root = JsonParser.parseString(cleanJson).asJsonObject
            val arr = root.getAsJsonArray("keyPoints")
            arr.map { el ->
                val obj = el.asJsonObject
                val pointsArr = obj.getAsJsonArray("points")
                KeyPoint(
                    topic = obj.get("topic")?.asString ?: "",
                    points = pointsArr.map { it.asString }
                )
            }.filter { it.topic.isNotBlank() && it.points.isNotEmpty() }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing key points: ${e.message}")
            emptyList()
        }
    }

    private fun parseFormulae(json: String): List<Formula> {
        return try {
            val cleanJson = extractJson(json)
            val root = JsonParser.parseString(cleanJson).asJsonObject
            val arr = root.getAsJsonArray("formulae")
            arr.map { el ->
                val obj = el.asJsonObject
                Formula(
                    name = obj.get("name")?.asString ?: "",
                    formula = obj.get("formula")?.asString ?: "",
                    explanation = obj.get("explanation")?.asString ?: "",
                    topic = obj.get("topic")?.asString ?: ""
                )
            }.filter { it.formula.isNotBlank() }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing formulae: ${e.message}")
            emptyList()
        }
    }

    private fun parseExamQuestions(json: String): List<ExamQuestion> {
        return try {
            val cleanJson = extractJson(json)
            val root = JsonParser.parseString(cleanJson).asJsonObject
            val arr = root.getAsJsonArray("questions")
            arr.map { el ->
                val obj = el.asJsonObject
                val typeStr = obj.get("type")?.asString ?: "SHORT_ANSWER"
                val type = try { QuestionType.valueOf(typeStr) } catch (e: Exception) { QuestionType.SHORT_ANSWER }
                val optionsArr = obj.getAsJsonArray("options")
                ExamQuestion(
                    question = obj.get("question")?.asString ?: "",
                    answer = obj.get("answer")?.asString ?: "",
                    type = type,
                    marks = obj.get("marks")?.asInt ?: 2,
                    options = optionsArr?.map { it.asString } ?: emptyList()
                )
            }.filter { it.question.isNotBlank() }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing exam questions: ${e.message}")
            emptyList()
        }
    }

    /** Strip markdown code fences if Gemini wraps JSON in ```json ... ``` */
    private fun extractJson(text: String): String {
        val trimmed = text.trim()
        return when {
            trimmed.startsWith("```json") -> trimmed.removePrefix("```json").removeSuffix("```").trim()
            trimmed.startsWith("```") -> trimmed.removePrefix("```").removeSuffix("```").trim()
            else -> trimmed
        }
    }
}
