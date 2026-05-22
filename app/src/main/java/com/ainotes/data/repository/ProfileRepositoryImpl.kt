package com.ainotes.data.repository

import android.util.Log
import com.ainotes.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProfileRepository {

    override suspend fun getUserProfile(userId: String): Result<UserProfile?> {
        return try {
            val docRef = firestore.collection("users").document(userId)
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val profile = UserProfile.fromMap(snapshot.data, userId)
                Result.success(profile)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error getting user profile for user: $userId", e)
            Result.failure(e)
        }
    }

    override suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            val docRef = firestore.collection("users").document(profile.uid)
            docRef.set(profile.toMap()).await()
            Log.d("ProfileRepository", "Successfully saved user profile for user: ${profile.uid}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error saving user profile for user: ${profile.uid}", e)
            Result.failure(e)
        }
    }
}
