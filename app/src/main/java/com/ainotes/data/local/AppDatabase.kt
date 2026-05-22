package com.ainotes.data.local

import androidx.room.*
import com.ainotes.data.model.NoteSession
import com.ainotes.data.model.StudyNotesConverters
import kotlinx.coroutines.flow.Flow

// ─── DAO ──────────────────────────────────────────────────────────────────────

@Dao
interface NoteSessionDao {

    @Query("SELECT * FROM note_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<NoteSession>>

    @Query("SELECT * FROM note_sessions WHERE id = :id")
    suspend fun getSessionById(id: String): NoteSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: NoteSession)

    @Delete
    suspend fun deleteSession(session: NoteSession)

    @Query("DELETE FROM note_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: String)

    @Query("DELETE FROM note_sessions")
    suspend fun clearAllSessions()

    @Query("SELECT * FROM note_sessions WHERE title LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchSessions(query: String): Flow<List<NoteSession>>

    @Query("UPDATE note_sessions SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: String, isSaved: Boolean)
}

// ─── Database ─────────────────────────────────────────────────────────────────

@Database(
    entities = [NoteSession::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(StudyNotesConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteSessionDao(): NoteSessionDao

    companion object {
        const val DATABASE_NAME = "ainotes_db"
    }
}
