package com.ainotes.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ainotes.data.local.ThemePreferences
import com.ainotes.data.model.UserProfile
import com.ainotes.data.repository.AuthRepository
import com.ainotes.data.repository.GeminiRepository
import com.ainotes.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val educationLevel: String = "", // "School" or "College"
    val classLevel: String = "",
    val degree: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val isProfileLoaded: Boolean = false,
    // API Key management
    val apiKeyInput: String = "",
    val hasUserApiKey: Boolean = false,
    val apiKeyMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val themePreferences: ThemePreferences,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    val themeMode: StateFlow<String> = themePreferences.themeModeFlow

    fun setThemeMode(mode: String) {
        themePreferences.setThemeMode(mode)
    }

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        // Load whether user already has a saved personal API key
        _uiState.value = _uiState.value.copy(
            hasUserApiKey = geminiRepository.hasUserApiKey()
        )
    }

    fun loadUserProfile() {
        val currentUser = authRepository.currentUser
        if (currentUser == null) {
            _uiState.value = _uiState.value.copy(error = "User not logged in.")
            return
        }

        _uiState.value = _uiState.value.copy(
            uid = currentUser.uid,
            email = currentUser.email ?: "",
            name = currentUser.displayName ?: "",
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = profileRepository.getUserProfile(currentUser.uid)
            val profile = result.getOrNull()
            if (profile != null) {
                _uiState.value = _uiState.value.copy(
                    name = profile.name.ifEmpty { currentUser.displayName ?: "" },
                    email = profile.email.ifEmpty { currentUser.email ?: "" },
                    educationLevel = profile.educationLevel,
                    classLevel = profile.classLevel,
                    degree = profile.degree,
                    isLoading = false,
                    isProfileLoaded = true
                )
            } else {
                // No profile in Firestore yet, pre-populate from Firebase Auth
                _uiState.value = _uiState.value.copy(
                    name = currentUser.displayName ?: "",
                    email = currentUser.email ?: "",
                    isLoading = false,
                    isProfileLoaded = true
                )
            }
        }
    }

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onEducationLevelChanged(level: String) {
        // Reset class and degree if education level changes
        _uiState.value = _uiState.value.copy(
            educationLevel = level,
            classLevel = "",
            degree = "",
            error = null
        )
    }

    fun onClassLevelChanged(classLevel: String) {
        _uiState.value = _uiState.value.copy(classLevel = classLevel, error = null)
    }

    fun onDegreeChanged(degree: String) {
        _uiState.value = _uiState.value.copy(degree = degree, error = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun saveProfile() {
        val state = _uiState.value
        val name = state.name.trim()
        val email = state.email.trim()
        val edLevel = state.educationLevel
        val classLvl = state.classLevel
        val degree = state.degree.trim()

        if (name.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Full Name is required.")
            return
        }
        if (email.isEmpty() || !email.contains("@")) {
            _uiState.value = _uiState.value.copy(error = "A valid Email Address is required.")
            return
        }
        if (edLevel.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Please select either School or College.")
            return
        }
        if (classLvl.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Please select your Class.")
            return
        }
        if (edLevel == "College" && degree.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Please enter your Degree (e.g. B.Tech).")
            return
        }

        _uiState.value = _uiState.value.copy(isSaving = true, error = null)

        viewModelScope.launch {
            val userProfile = UserProfile(
                uid = state.uid,
                name = name,
                email = email,
                educationLevel = edLevel,
                classLevel = classLvl,
                degree = if (edLevel == "College") degree else "",
                profileCompleted = true
            )

            val result = profileRepository.saveUserProfile(userProfile)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to save profile. Please try again."
                )
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    // ─── API Key Management ───────────────────────────────────────────────────

    fun onApiKeyInputChanged(key: String) {
        _uiState.value = _uiState.value.copy(apiKeyInput = key, apiKeyMessage = null)
    }

    fun saveApiKey() {
        val key = _uiState.value.apiKeyInput.trim()
        if (key.isBlank()) {
            _uiState.value = _uiState.value.copy(apiKeyMessage = "Please enter a valid API key.")
            return
        }
        if (!key.startsWith("AIza")) {
            _uiState.value = _uiState.value.copy(apiKeyMessage = "Invalid API key format. Keys start with 'AIza'.")
            return
        }
        geminiRepository.saveUserApiKey(key)
        _uiState.value = _uiState.value.copy(
            hasUserApiKey = true,
            apiKeyInput = "",
            apiKeyMessage = "✅ Your API key has been saved! Rate limit errors should no longer occur."
        )
    }

    fun clearApiKey() {
        geminiRepository.clearUserApiKey()
        _uiState.value = _uiState.value.copy(
            hasUserApiKey = false,
            apiKeyInput = "",
            apiKeyMessage = "API key removed. Reverted to the shared built-in key."
        )
    }
}
