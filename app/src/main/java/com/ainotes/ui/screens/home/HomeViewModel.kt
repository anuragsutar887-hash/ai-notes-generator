package com.ainotes.ui.screens.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ainotes.data.model.*
import com.ainotes.data.repository.GeminiRepository
import com.ainotes.data.repository.GeminiResult
import com.ainotes.data.repository.ProfileRepository
import com.ainotes.data.repository.SessionRepository
import com.ainotes.data.repository.FirebaseSyncRepository
import com.ainotes.util.FileHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val selectedUris: List<Uri> = emptyList(),
    val selectedFileNames: List<String> = emptyList(),
    val pastedText: String = "",
    val selectedMode: GenerationMode = GenerationMode.FLASHCARDS,
    val customQuery: String = "",
    val isProcessing: Boolean = false,
    val processingStage: String = "",
    val processingProgress: Float = 0f,
    val error: String? = null,
    val lastSessionId: String? = null,
    val userName: String = "",
    val recentSessions: List<NoteSession> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val geminiRepository: GeminiRepository,
    private val sessionRepository: SessionRepository,
    private val fileHelper: FileHelper,
    private val firebaseSyncRepository: FirebaseSyncRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var progressJob: kotlinx.coroutines.Job? = null

    init {
        loadUserName()
        loadRecentSessions()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val result = profileRepository.getUserProfile(uid)
            val profile = result.getOrNull()
            if (profile != null && profile.name.isNotBlank()) {
                val firstName = profile.name.trim().split(" ").firstOrNull() ?: profile.name
                _uiState.value = _uiState.value.copy(userName = firstName)
            }
        }
    }

    private fun loadRecentSessions() {
        viewModelScope.launch {
            sessionRepository.getAllSessions().collectLatest { sessions ->
                val startOfToday = java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val todaySessions = sessions.filter { it.createdAt >= startOfToday }
                _uiState.value = _uiState.value.copy(
                    recentSessions = todaySessions
                )
            }
        }
    }

    fun onFilesSelected(uris: List<Uri>) {
        val names = uris.map { fileHelper.getFileName(it) }
        _uiState.value = _uiState.value.copy(
            selectedUris = uris,
            selectedFileNames = names,
            pastedText = "",
            error = null
        )
    }

    fun onPastedTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(
            pastedText = text,
            selectedUris = emptyList(),
            selectedFileNames = emptyList()
        )
    }

    fun onModeSelected(mode: GenerationMode) {
        _uiState.value = _uiState.value.copy(selectedMode = mode)
    }

    fun onCustomQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(customQuery = query)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedUris = emptyList(),
            selectedFileNames = emptyList(),
            pastedText = "",
            error = null
        )
    }

    fun startProcessing() {
        val state = _uiState.value
        val hasPastedText = state.pastedText.isNotBlank()
        val hasFiles = state.selectedUris.isNotEmpty()

        if (!hasPastedText && !hasFiles) {
            _uiState.value = state.copy(error = "Please select a file or paste some text first.")
            return
        }

        if (state.selectedMode == GenerationMode.CUSTOM && state.customQuery.isBlank()) {
            _uiState.value = state.copy(error = "Please enter your custom query.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isProcessing = true,
                processingStage = "Starting…",
                processingProgress = 0f,
                error = null,
                lastSessionId = null
            )

            try {
                val flow = if (hasPastedText) {
                    geminiRepository.processText(
                        text = state.pastedText,
                        mode = state.selectedMode,
                        customQuery = state.customQuery
                    )
                } else {
                    // Process first file (multi-file: iterate all)
                    geminiRepository.processDocument(
                        uri = state.selectedUris.first(),
                        mode = state.selectedMode,
                        customQuery = state.customQuery,
                        inputType = fileHelper.detectInputType(state.selectedUris.first())
                    )
                }

                flow.collect { result ->
                    progressJob?.cancel() // Cancel any ongoing progress animation
                    when (result) {
                        is GeminiResult.Progress -> {
                            val prog = result.progress
                            val totalChunks = prog.totalChunks
                            val chunkIndex = prog.chunkIndex

                            if (totalChunks > 0) {
                                // Calculate the target progress slice for this chunk
                                val startProgress = 0.10f + ((chunkIndex - 1).toFloat() / totalChunks) * 0.80f
                                val endProgress = 0.10f + (chunkIndex.toFloat() / totalChunks) * 0.80f
                                
                                progressJob = viewModelScope.launch {
                                    val durationMs = 15_000f // 15 seconds target per chunk
                                    val steps = 150
                                    val delayMs = 100L
                                    val targetProgress = endProgress - 0.03f
                                    val increment = (targetProgress - startProgress) / steps
                                    
                                    var currentProgress = startProgress
                                    _uiState.value = _uiState.value.copy(
                                        processingStage = "${prog.stage} ($chunkIndex/$totalChunks)",
                                        processingProgress = currentProgress
                                    )
                                    
                                    for (i in 0 until steps) {
                                        kotlinx.coroutines.delay(delayMs)
                                        currentProgress += increment
                                        if (currentProgress > targetProgress) {
                                            currentProgress = targetProgress
                                        }
                                        _uiState.value = _uiState.value.copy(
                                            processingProgress = currentProgress
                                        )
                                    }
                                }
                            } else {
                                // For initial stages like "Extracting text" or "Running OCR"
                                val fraction = when {
                                    prog.stage.contains("OCR", ignoreCase = true) -> 0.10f
                                    else -> 0.05f
                                }
                                _uiState.value = _uiState.value.copy(
                                    processingStage = prog.stage,
                                    processingProgress = fraction
                                )
                            }
                        }
                        is GeminiResult.Success -> {
                            val session = NoteSession(
                                title = if (hasPastedText) "Pasted Text" else state.selectedFileNames.firstOrNull() ?: "Document",
                                inputType = if (hasPastedText) "TEXT" else fileHelper.detectInputType(state.selectedUris.first()).name,
                                mode = state.selectedMode.name,
                                customQuery = state.customQuery,
                                notes = result.notes
                            )

                            // Firebase Sync
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            if (currentUser != null) {
                                val userId = currentUser.uid
                                _uiState.value = _uiState.value.copy(
                                    processingStage = "Uploading to Cloud Storage…",
                                    processingProgress = 0.95f
                                )
                                var downloadUrl: String? = null
                                if (!hasPastedText && state.selectedUris.isNotEmpty()) {
                                    downloadUrl = firebaseSyncRepository.uploadDocument(
                                        userId = userId,
                                        sessionId = session.id,
                                        uri = state.selectedUris.first(),
                                        fileName = state.selectedFileNames.firstOrNull() ?: "document"
                                    )
                                }
                                _uiState.value = _uiState.value.copy(
                                    processingStage = "Saving to Cloud Database…",
                                    processingProgress = 0.98f
                                )
                                firebaseSyncRepository.syncSessionToFirestore(
                                    userId = userId,
                                    session = session,
                                    documentUrl = downloadUrl
                                )
                            }

                            // Save to Room
                            sessionRepository.saveSession(session)
                            _uiState.value = _uiState.value.copy(
                                isProcessing = false,
                                lastSessionId = session.id
                            )
                        }
                        is GeminiResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isProcessing = false,
                                error = result.message
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearLastSessionId() {
        _uiState.value = _uiState.value.copy(lastSessionId = null)
    }
}
