package com.ainotes.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * OCR helper using Google ML Kit.
 * Works on printed text, handwritten notes, diagrams with labels.
 * Processes images from URI (camera, gallery, file manager).
 */
@Singleton
class OcrHelper @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "OcrHelper"
    }

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Extract text from an image URI (supports JPG, PNG, WEBP, BMP).
     * This works for both printed text and handwritten notes.
     */
    suspend fun extractTextFromImage(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            recognizeText(image)
        } catch (e: Exception) {
            Log.e(TAG, "Error in OCR: ${e.message}", e)
            throw e
        }
    }

    private suspend fun recognizeText(image: InputImage): String =
        suspendCancellableCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val extractedText = buildString {
                        for (block in visionText.textBlocks) {
                            append(block.text)
                            append("\n\n")
                        }
                    }.trim()
                    continuation.resume(extractedText)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "ML Kit text recognition failed", exception)
                    continuation.resumeWithException(exception)
                }
        }

    fun release() {
        recognizer.close()
    }
}
