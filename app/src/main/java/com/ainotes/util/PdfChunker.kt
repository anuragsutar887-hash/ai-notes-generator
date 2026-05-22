package com.ainotes.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extracts text from PDF files, supports unlimited size by chunking pages.
 * Uses PdfBox-Android for reliable text extraction.
 */
@Singleton
class PdfChunker @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "PdfChunker"
        const val PAGES_PER_CHUNK = 100  // Process up to 100 pages in a single chunk to minimize API rate limit issues
    }

    init {
        PDFBoxResourceLoader.init(context)
    }

    /**
     * Extracts all text from a PDF, returns a list of chunks (each ~50 pages).
     * Progress callback: (currentPage, totalPages)
     */
    suspend fun extractChunks(
        uri: Uri,
        onProgress: (current: Int, total: Int) -> Unit = { _, _ -> }
    ): List<String> = withContext(Dispatchers.IO) {
        val chunks = mutableListOf<String>()
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val document = PDDocument.load(inputStream)
                document.use { doc ->
                    val totalPages = doc.numberOfPages
                    Log.d(TAG, "PDF has $totalPages pages")

                    val stripper = PDFTextStripper()

                    var startPage = 1
                    while (startPage <= totalPages) {
                        val endPage = minOf(startPage + PAGES_PER_CHUNK - 1, totalPages)
                        stripper.startPage = startPage
                        stripper.endPage = endPage

                        val chunkText = stripper.getText(doc).trim()
                        if (chunkText.isNotBlank()) {
                            chunks.add(chunkText)
                        }

                        onProgress(endPage, totalPages)
                        startPage += PAGES_PER_CHUNK
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting PDF: ${e.message}", e)
            throw e
        }
        chunks
    }

    /**
     * Get the total page count of a PDF without fully loading it.
     */
    suspend fun getPageCount(uri: Uri): Int = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                PDDocument.load(inputStream).use { doc ->
                    doc.numberOfPages
                }
            } ?: 0
        } catch (e: Exception) {
            Log.e(TAG, "Error getting page count: ${e.message}")
            0
        }
    }
}
