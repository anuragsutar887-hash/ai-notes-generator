package com.ainotes.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileHelper @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "FileHelper"
    }

    /** Get the display name of a file from URI */
    fun getFileName(uri: Uri): String {
        var name = "Unknown file"
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    name = cursor.getString(nameIndex)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting filename: ${e.message}")
        }
        return name
    }

    /** Get file size in bytes */
    fun getFileSize(uri: Uri): Long {
        var size = 0L
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst() && sizeIndex >= 0) {
                    size = cursor.getLong(sizeIndex)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file size: ${e.message}")
        }
        return size
    }

    /** Detect file type from MIME type */
    fun getMimeType(uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "application/octet-stream"
    }

    /** Read plain text from a text URI */
    suspend fun readTextFile(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                stream.bufferedReader().readText()
            } ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error reading text file: ${e.message}")
            ""
        }
    }

    /** Format file size to human-readable */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
            else -> "${"%.1f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB"
        }
    }

    /** Determine InputType from MIME type */
    fun detectInputType(uri: Uri): com.ainotes.data.model.InputType {
        val mime = getMimeType(uri)
        return when {
            mime == "application/pdf" -> com.ainotes.data.model.InputType.PDF
            mime.startsWith("image/") -> com.ainotes.data.model.InputType.IMAGE
            mime.startsWith("text/") -> com.ainotes.data.model.InputType.TEXT
            else -> com.ainotes.data.model.InputType.TEXT
        }
    }
}
