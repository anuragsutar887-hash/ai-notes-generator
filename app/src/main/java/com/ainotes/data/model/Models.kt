package com.ainotes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// ─── Generation Modes ────────────────────────────────────────────────────────

enum class GenerationMode(val displayName: String, val emoji: String) {
    FLASHCARDS("Flashcards", "📇"),
    KEY_POINTS("Key Points", "📌"),
    FORMULAE("Formulae", "🔢"),
    EXAM_QUESTIONS("Exam Questions", "❓"),
    CUSTOM("Custom Query", "💬")
}

// ─── Input Source Type ────────────────────────────────────────────────────────

enum class InputType {
    PDF, IMAGE, TEXT, HANDWRITTEN
}

// ─── Core Data Models ─────────────────────────────────────────────────────────

data class Flashcard(
    val id: String = java.util.UUID.randomUUID().toString(),
    val front: String,       // Question side
    val back: String,        // Answer side
    val topic: String = ""
)

data class KeyPoint(
    val id: String = java.util.UUID.randomUUID().toString(),
    val topic: String,
    val points: List<String>
)

data class Formula(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val formula: String,
    val explanation: String,
    val topic: String = ""
)

data class ExamQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String,
    val answer: String,
    val type: QuestionType,
    val options: List<String> = emptyList(),  // For MCQ
    val marks: Int = 2
)

enum class QuestionType { SHORT_ANSWER, LONG_ANSWER, MCQ, TRUE_FALSE }

// ─── Session Result Container ─────────────────────────────────────────────────

data class StudyNotes(
    val flashcards: List<Flashcard> = emptyList(),
    val keyPoints: List<KeyPoint> = emptyList(),
    val formulae: List<Formula> = emptyList(),
    val examQuestions: List<ExamQuestion> = emptyList(),
    val customResult: String = "",
    val summary: String = ""
)

// ─── Room Entity ──────────────────────────────────────────────────────────────

@Entity(tableName = "note_sessions")
@TypeConverters(StudyNotesConverters::class)
data class NoteSession(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val inputType: String,
    val mode: String,
    val customQuery: String = "",
    val notes: StudyNotes,
    val pageCount: Int = 0,
    val processingTimeMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false
)

// ─── Type Converters for Room ─────────────────────────────────────────────────

class StudyNotesConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromStudyNotes(notes: StudyNotes): String = gson.toJson(notes)

    @TypeConverter
    fun toStudyNotes(json: String): StudyNotes =
        gson.fromJson(json, StudyNotes::class.java) ?: StudyNotes()

    @TypeConverter
    fun fromStringList(list: List<String>): String = gson.toJson(list)

    @TypeConverter
    fun toStringList(json: String): List<String> =
        gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
}
