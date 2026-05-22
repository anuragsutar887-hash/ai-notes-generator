package com.ainotes.data.repository

import com.ainotes.data.model.UserProfile

interface ProfileRepository {
    suspend fun getUserProfile(userId: String): Result<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile): Result<Unit>
}
