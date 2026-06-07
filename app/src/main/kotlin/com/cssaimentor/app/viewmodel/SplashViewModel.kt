package com.cssaimentor.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cssaimentor.app.domain.repository.AuthRepository
import com.cssaimentor.app.domain.repository.UserPreferencesRepository
import com.cssaimentor.app.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashUiState(
    val targetRoute: String? = null
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1800)
            val completed = preferencesRepository.onboardingCompleted.first()
            val user = authRepository.currentUser.first()
            val route = when {
                !completed -> Route.Onboarding.route
                user != null -> Route.Home.route
                else -> Route.Login.route
            }
            _uiState.value = SplashUiState(targetRoute = route)
        }
    }
}

