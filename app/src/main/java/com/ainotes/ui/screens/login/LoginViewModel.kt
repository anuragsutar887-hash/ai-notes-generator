package com.ainotes.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ainotes.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isSignUpMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: FirebaseUser? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUserFlow.collect { user ->
                _uiState.value = _uiState.value.copy(currentUser = user)
            }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isSignUpMode = !_uiState.value.isSignUpMode,
            error = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    fun signInWithGoogle(idToken: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun authenticateWithEmail() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password.trim()

        if (email.isEmpty()) {
            showError("Please enter your email address.")
            return
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            showError("Password must be at least 6 characters.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = if (_uiState.value.isSignUpMode) {
                authRepository.signUpWithEmail(email, password)
            } else {
                authRepository.signInWithEmail(email, password)
            }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
