package com.ainotes.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ainotes.data.model.NoteSession
import com.ainotes.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val sessions: StateFlow<List<NoteSession>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            val baseFlow = if (query.isBlank()) {
                sessionRepository.getAllSessions()
            } else {
                sessionRepository.searchSessions(query)
            }
            baseFlow.map { list -> list.filter { it.isSaved } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteSession(session: NoteSession) {
        viewModelScope.launch {
            sessionRepository.deleteSession(session)
        }
    }

    fun toggleSavedStatus(session: NoteSession) {
        viewModelScope.launch {
            sessionRepository.updateSavedStatus(session.id, !session.isSaved)
        }
    }
}
