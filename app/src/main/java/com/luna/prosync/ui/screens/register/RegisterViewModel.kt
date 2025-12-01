package com.luna.prosync.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.prosync.data.local.TokenManager
import com.luna.prosync.data.remote.dto.UserRegisterRequest
import com.luna.prosync.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    private val _navigateToProjects = MutableStateFlow(false)
    val navigateToProjects: StateFlow<Boolean> = _navigateToProjects.asStateFlow()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, error = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPassword = password, error = null) }
    }

    fun onNavigationDone() {
        _navigateToLogin.value = false
        _navigateToProjects.value = false
    }

    fun onRegisterClick() {
        val state = _uiState.value

        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }
        if (state.username.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val request = UserRegisterRequest(
                    username = state.username,
                    email = state.email,
                    password = state.password
                )

                val response = authRepository.register(request)

                if (response.isSuccessful) {
                    _navigateToLogin.value = true
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error en el registro"
                    _uiState.update { it.copy(error = errorBody) }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error de conexión") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loginWithGoogle(token: String, username: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val authResponse = authRepository.loginWithGoogle(token, username)
                tokenManager.saveToken(authResponse.token)
                _navigateToProjects.value = true
                _uiState.update { it.copy(showUsernameDialog = false, googleToken = null) }
            } catch (e: Exception) {
                if (e.message?.contains("USER_NOT_FOUND") == true || e.message?.contains("404") == true) {
                    _uiState.update { it.copy(showUsernameDialog = true, googleToken = token, isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = "Error Google: ${e.message}", isLoading = false) }
                }
            }
        }
    }

    fun onGoogleUsernameSubmit(username: String) {
        val token = _uiState.value.googleToken
        if (token != null) {
            loginWithGoogle(token, username)
        }
    }

    fun onDismissUsernameDialog() {
        _uiState.update { it.copy(showUsernameDialog = false, googleToken = null) }
    }
}