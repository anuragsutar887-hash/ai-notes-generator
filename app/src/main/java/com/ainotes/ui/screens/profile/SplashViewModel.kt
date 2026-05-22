package com.ainotes.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ainotes.data.local.UserPreferences
import com.ainotes.data.repository.AuthRepository
import com.ainotes.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashEvent {
    object NavigateToLogin : SplashEvent()
    object NavigateToHome : SplashEvent()
    object NavigateToProfileSetup : SplashEvent()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<SplashEvent>()
    val eventFlow: SharedFlow<SplashEvent> = _eventFlow.asSharedFlow()

    val isOnboardingCompleted: Boolean
        get() = userPreferences.isOnboardingCompleted()

    init {
        // Only run automatic check-routing on startup if onboarding is completed
        if (userPreferences.isOnboardingCompleted()) {
            checkRouting()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.setOnboardingCompleted(true)
            _eventFlow.emit(SplashEvent.NavigateToLogin)
        }
    }

    fun checkRouting() {
        viewModelScope.launch {
            // Give a tiny delay for the beautiful pulsing animation to breathe
            kotlinx.coroutines.delay(300L)
            
            val currentUser = authRepository.currentUser
            if (currentUser == null) {
                _eventFlow.emit(SplashEvent.NavigateToLogin)
            } else {
                val result = profileRepository.getUserProfile(currentUser.uid)
                val profile = result.getOrNull()
                if (profile != null && profile.profileCompleted) {
                    _eventFlow.emit(SplashEvent.NavigateToHome)
                } else {
                    _eventFlow.emit(SplashEvent.NavigateToProfileSetup)
                }
            }
        }
    }
}
