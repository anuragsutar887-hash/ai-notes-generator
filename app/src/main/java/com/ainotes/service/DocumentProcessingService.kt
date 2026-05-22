package com.ainotes.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ainotes.R
import com.ainotes.data.model.GenerationMode
import com.ainotes.data.model.InputType
import com.ainotes.data.model.NoteSession
import com.ainotes.data.repository.GeminiRepository
import com.ainotes.data.repository.GeminiResult
import com.ainotes.data.repository.SessionRepository
import com.ainotes.util.FileHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Foreground service for processing very large documents.
 * Shows a persistent notification with progress while Gemini analyzes the file.
 */
@AndroidEntryPoint
class DocumentProcessingService : Service() {

    companion object {
        const val CHANNEL_ID = "ainotes_processing"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_URI = "extra_uri"
        const val EXTRA_MODE = "extra_mode"
        const val EXTRA_QUERY = "extra_query"
        const val EXTRA_INPUT_TYPE = "extra_input_type"
    }

    @Inject lateinit var geminiRepository: GeminiRepository
    @Inject lateinit var sessionRepository: SessionRepository
    @Inject lateinit var fileHelper: FileHelper

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uriStr = intent?.getStringExtra(EXTRA_URI) ?: return START_NOT_STICKY
        val modeStr = intent.getStringExtra(EXTRA_MODE) ?: return START_NOT_STICKY
        val query = intent.getStringExtra(EXTRA_QUERY) ?: ""
        val inputTypeStr = intent.getStringExtra(EXTRA_INPUT_TYPE) ?: InputType.PDF.name

        val uri = Uri.parse(uriStr)
        val mode = try { GenerationMode.valueOf(modeStr) } catch (e: Exception) { GenerationMode.FLASHCARDS }
        val inputType = try { InputType.valueOf(inputTypeStr) } catch (e: Exception) { InputType.PDF }

        startForeground(NOTIFICATION_ID, buildNotification("Preparing document…", 0))

        serviceScope.launch {
            geminiRepository.processDocument(uri, mode, query, inputType).collect { result ->
                when (result) {
                    is GeminiResult.Progress -> {
                        val prog = result.progress
                        val progressPercent = if (prog.totalChunks > 0)
                            ((prog.chunkIndex.toFloat() / prog.totalChunks) * 100).toInt() else 0
                        updateNotification(prog.stage, progressPercent)
                    }
                    is GeminiResult.Success -> {
                        val session = NoteSession(
                            title = fileHelper.getFileName(uri),
                            inputType = inputType.name,
                            mode = mode.name,
                            customQuery = query,
                            notes = result.notes
                        )
                        sessionRepository.saveSession(session)
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                    is GeminiResult.Error -> {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Document Processing",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shown while AInotes analyzes your document"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(text: String, progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AInotes — Analyzing Document")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(text: String, progress: Int) {
        val notification = buildNotification(text, progress)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }
}
