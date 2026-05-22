package com.ainotes.data.repository

import android.app.Activity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: FirebaseUser?
    val currentUserFlow: StateFlow<FirebaseUser?>

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser>
    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser>

    fun startPhoneVerification(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )

    suspend fun signInWithPhoneCredential(
        verificationId: String,
        code: String
    ): Result<FirebaseUser>

    fun signOut()
}
