package com.ainotes.ui.screens.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ainotes.data.model.NoteSession
import com.ainotes.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultsUiState(
    val session: NoteSession? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            try {
                val session = sessionRepository.getSessionById(sessionId)
                _uiState.value = ResultsUiState(session = session, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = ResultsUiState(isLoading = false, error = e.message)
            }
        }
    }

    fun deleteSession(onDeleted: () -> Unit) {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            sessionRepository.deleteSession(session)
            onDeleted()
        }
    }

    fun toggleSavedStatus() {
        val currentSession = _uiState.value.session ?: return
        val newSavedStatus = !currentSession.isSaved
        viewModelScope.launch {
            sessionRepository.updateSavedStatus(currentSession.id, newSavedStatus)
            _uiState.value = _uiState.value.copy(
                session = currentSession.copy(isSaved = newSavedStatus)
            )
        }
    }
}
