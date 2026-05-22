package com.ainotes.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val sessionRepository: SessionRepository
) : AuthRepository {

    private val _currentUserFlow = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    override val currentUserFlow: StateFlow<FirebaseUser?> = _currentUserFlow.asStateFlow()

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    private var lastUserId: String? = firebaseAuth.currentUser?.uid

    init {
        firebaseAuth.addAuthStateListener { auth ->
            val newUser = auth.currentUser
            _currentUserFlow.value = newUser

            // If the user identity changed (e.g. logged out, or logged in as a different user)
            if (newUser?.uid != lastUserId) {
                val oldUid = lastUserId
                val newUid = newUser?.uid
                lastUserId = newUid
                Log.d("AuthRepository", "User identity changed from $oldUid to $newUid. Wiping local session database.")
                
                @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        sessionRepository.clearAllSessions()
                        Log.d("AuthRepository", "Successfully wiped local database after user identity change.")
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Failed to wipe local database on user identity change", e)
                    }
                }
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to sign in with Google: user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to sign in: user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to create account: user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun startPhoneVerification(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun signInWithPhoneCredential(
        verificationId: String,
        code: String
    ): Result<FirebaseUser> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to sign in with Phone: user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
