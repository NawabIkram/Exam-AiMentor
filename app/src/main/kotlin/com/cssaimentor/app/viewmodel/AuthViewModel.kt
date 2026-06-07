package com.cssaimentor.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cssaimentor.app.domain.repository.AuthRepository
import com.cssaimentor.app.utils.AppResult
import com.cssaimentor.app.utils.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val info: String? = null,
    val authenticated: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateName(value: String) = _uiState.update { it.copy(name = value, error = null) }
    fun updateEmail(value: String) = _uiState.update { it.copy(email = value, error = null) }
    fun updatePassword(value: String) = _uiState.update { it.copy(password = value, error = null) }
    fun consumeAuthEvent() = _uiState.update { it.copy(authenticated = false) }

    fun login() {
        val email = uiState.value.email.trim()
        val password = uiState.value.password
        val error = Validators.emailError(email) ?: Validators.passwordError(password)
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null, info = null) }
            when (val result = authRepository.login(email, password)) {
                is AppResult.Success -> _uiState.update { it.copy(loading = false, authenticated = true) }
                is AppResult.Error -> _uiState.update { it.copy(loading = false, error = result.message) }
            }
        }
    }

    fun signup() {
        val state = uiState.value
        val error = Validators.nameError(state.name) ?: Validators.emailError(state.email.trim()) ?: Validators.passwordError(state.password)
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null, info = null) }
            when (val result = authRepository.signup(state.name.trim(), state.email.trim(), state.password)) {
                is AppResult.Success -> _uiState.update { it.copy(loading = false, authenticated = true) }
                is AppResult.Error -> _uiState.update { it.copy(loading = false, error = result.message) }
            }
        }
    }

    fun forgotPassword() {
        val email = uiState.value.email.trim()
        val error = Validators.emailError(email)
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null, info = null) }
            when (val result = authRepository.sendPasswordReset(email)) {
                is AppResult.Success -> _uiState.update { it.copy(loading = false, info = "Password reset email requested.") }
                is AppResult.Error -> _uiState.update { it.copy(loading = false, error = result.message) }
            }
        }
    }

    fun googleSignInNotConfigured() {
        _uiState.update { it.copy(error = "Add a real Firebase Web Client ID to enable Google sign-in.") }
    }
}

