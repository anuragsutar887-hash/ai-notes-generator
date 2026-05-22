package com.ainotes.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ainotes.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSyncRepository @Inject constructor(
    private val context: Context,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    suspend fun uploadDocument(
        userId: String,
        sessionId: String,
        uri: Uri,
        fileName: String
    ): String? {
        return try {
            val storageRef = storage.reference
                .child("users")
                .child(userId)
                .child("sessions")
                .child(sessionId)
                .child(fileName)

            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("FirebaseSync", "Cannot open stream for Uri: $uri")
                return null
            }

            // Upload stream
            storageRef.putStream(inputStream).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Log.d("FirebaseSync", "Uploaded document successfully. URL: $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error uploading document", e)
            null
        }
    }

    suspend fun syncSessionToFirestore(
        userId: String,
        session: NoteSession,
        documentUrl: String? = null
    ): Boolean {
        return try {
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("sessions")
                .document(session.id)

            val sessionMap = sessionToMap(session, documentUrl)
            docRef.set(sessionMap).await()
            Log.d("FirebaseSync", "Synced session ${session.id} to Firestore")
            true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error syncing session to Firestore", e)
            false
        }
    }

    private fun sessionToMap(session: NoteSession, documentUrl: String?): Map<String, Any?> {
        return mapOf(
            "id" to session.id,
            "title" to session.title,
            "inputType" to session.inputType,
            "mode" to session.mode,
            "customQuery" to session.customQuery,
            "pageCount" to session.pageCount,
            "processingTimeMs" to session.processingTimeMs,
            "createdAt" to session.createdAt,
            "documentUrl" to documentUrl,
            "notes" to studyNotesToMap(session.notes)
        )
    }

    private fun studyNotesToMap(notes: StudyNotes): Map<String, Any?> {
        return mapOf(
            "flashcards" to notes.flashcards.map { it.toMap() },
            "keyPoints" to notes.keyPoints.map { it.toMap() },
            "formulae" to notes.formulae.map { it.toMap() },
            "examQuestions" to notes.examQuestions.map { it.toMap() },
            "customResult" to notes.customResult,
            "summary" to notes.summary
        )
    }

    private fun Flashcard.toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "front" to front,
            "back" to back,
            "topic" to topic
        )
    }

    private fun KeyPoint.toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "topic" to topic,
            "points" to points
        )
    }

    private fun Formula.toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "formula" to formula,
            "explanation" to explanation,
            "topic" to topic
        )
    }

    private fun ExamQuestion.toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "question" to question,
            "answer" to answer,
            "type" to type.name,
            "options" to options,
            "marks" to marks
        )
    }
}
