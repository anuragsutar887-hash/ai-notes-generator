package com.ainotes.data.repository

import com.ainotes.data.local.NoteSessionDao
import com.ainotes.data.model.NoteSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val dao: NoteSessionDao
) {
    fun getAllSessions(): Flow<List<NoteSession>> = dao.getAllSessions()

    suspend fun getSessionById(id: String): NoteSession? = dao.getSessionById(id)

    suspend fun saveSession(session: NoteSession) = dao.insertSession(session)

    suspend fun deleteSession(session: NoteSession) = dao.deleteSession(session)

    suspend fun deleteSessionById(id: String) = dao.deleteSessionById(id)

    suspend fun clearAllSessions() = dao.clearAllSessions()

    fun searchSessions(query: String): Flow<List<NoteSession>> = dao.searchSessions(query)

    suspend fun updateSavedStatus(id: String, isSaved: Boolean) = dao.updateSavedStatus(id, isSaved)
}
