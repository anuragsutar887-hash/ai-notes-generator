package com.ainotes.data.repository

import android.net.Uri
import android.util.Log
import com.ainotes.data.model.*
import com.ainotes.util.FileHelper
import com.ainotes.util.OcrHelper
import com.ainotes.util.PdfChunker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.gson.JsonParser
import javax.inject.Inject
import javax.inject.Singleton

data class ProcessingProgress(
    val stage: String,
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
    }

    private val functions = FirebaseFunctions.getInstance()
    private val gson = Gson()

    /**
     * Calls the Firebase Cloud Function `generateNotes`.
     * The API key lives on the server — users never see or configure it.
     * Only authenticated users can call this function.
     */
    private suspend fun generateWithBackend(prompt: String): String = withContext(Dispatchers.IO) {
        val user = FirebaseAuth.getInstance().currentUser
            ?: throw IllegalStateException("You must be signed in to generate notes.")

        try {
            val data = hashMapOf("prompt" to prompt)
            val result = functions
                .getHttpsCallable("generateNotes")
                .call(data)
                .await()

            @Suppress("UNCHECKED_CAST")
            val resultData = result.data as? Map<String, Any>
            val text = resultData?.get("text") as? String
                ?: throw RuntimeException("Empty response from server.")

            Log.d(TAG, "Cloud Function response length: ${text.length}")
            text

        } catch (e: FirebaseFunctionsException) {
            Log.e(TAG, "Cloud Function error: ${e.code} — ${e.message}")
            val userMsg = when (e.code) {
                FirebaseFunctionsException.Code.UNAUTHENTICATED ->
                    "Please sign in to use AI Notes."
                FirebaseFunctionsException.Code.UNAVAILABLE ->
                    "AI service is temporarily busy. Please try again in a moment."
                FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED ->
                    "AI service is temporarily overloaded. Please try again in a few seconds."
                FirebaseFunctionsException.Code.DEADLINE_EXCEEDED ->
                    "Request timed out. Your document may be too large — try a smaller file."
                FirebaseFunctionsException.Code.INVALID_ARGUMENT ->
                    "Invalid content. Please check your document and try again."
                else -> e.message ?: "Something went wrong. Please try again."
            }
            throw RuntimeException(userMsg)
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

            val allFlashcards = mutableListOf<Flashcard>()
            val allKeyPoints = mutableListOf<KeyPoint>()
            val allFormulae = mutableListOf<Formula>()
            val allExamQuestions = mutableListOf<ExamQuestion>()
            val allCustomResults = StringBuilder()

            textChunks.forEachIndexed { index, chunk ->
                if (index > 0) delay(1500)

                emit(GeminiResult.Progress(
                    ProcessingProgress(
                        stage = "Analyzing document…",
                        chunkIndex = index + 1,
                        totalChunks = textChunks.size
                    )
                ))

                val prompt = buildPrompt(mode, customQuery, chunk, index + 1, textChunks.size)
                val responseText = generateWithBackend(prompt)

                Log.d(TAG, "Chunk ${index + 1} response length: ${responseText.length}")

                when (mode) {
                    GenerationMode.FLASHCARDS -> allFlashcards.addAll(parseFlashcards(responseText))
                    GenerationMode.KEY_POINTS -> allKeyPoints.addAll(parseKeyPoints(responseText))
                    GenerationMode.FORMULAE -> allFormulae.addAll(parseFormulae(responseText))
                    GenerationMode.EXAM_QUESTIONS -> allExamQuestions.addAll(parseExamQuestions(responseText))
                    GenerationMode.CUSTOM -> allCustomResults.append(responseText).append("\n\n")
                }
            }

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
            emit(GeminiResult.Error(e.message ?: "Something went wrong. Please try again."))
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
            emit(GeminiResult.Progress(ProcessingProgress("Analyzing…", chunkIndex = 1, totalChunks = 1)))
            val prompt = buildPrompt(mode, customQuery, text, 1, 1)
            val responseText = generateWithBackend(prompt)

            val notes = when (mode) {
                GenerationMode.FLASHCARDS -> StudyNotes(flashcards = parseFlashcards(responseText))
                GenerationMode.KEY_POINTS -> StudyNotes(keyPoints = parseKeyPoints(responseText))
                GenerationMode.FORMULAE -> StudyNotes(formulae = parseFormulae(responseText))
                GenerationMode.EXAM_QUESTIONS -> StudyNotes(examQuestions = parseExamQuestions(responseText))
                GenerationMode.CUSTOM -> StudyNotes(customResult = responseText)
            }
            emit(GeminiResult.Success(notes))
        } catch (e: Exception) {
            emit(GeminiResult.Error(e.message ?: "Something went wrong. Please try again."))
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
                    }
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

    private fun extractJson(text: String): String {
        val trimmed = text.trim()
        return when {
            trimmed.startsWith("```json") -> trimmed.removePrefix("```json").removeSuffix("```").trim()
            trimmed.startsWith("```") -> trimmed.removePrefix("```").removeSuffix("```").trim()
            else -> trimmed
        }
    }
}
