package com.luna.prosync.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.prosync.data.local.TokenManager
import com.luna.prosync.data.remote.dto.LoginRequest
import com.luna.prosync.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val _navigateToProjects = MutableStateFlow<Boolean>(false)
    val navigateToProjects: StateFlow<Boolean> = _navigateToProjects

    fun onUsernameChange(username: String) {
        _uiState.update { currentState ->
            currentState.copy(username = username, error = null)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { currentState ->
            currentState.copy(password = password, error = null)
        }
    }

    fun onLoginClick() {
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val loginRequest = LoginRequest(
                    username = currentState.username,
                    password = currentState.password
                )

                val authResponse = authRepository.login(loginRequest)

                Log.d("TOKEN_DEBUG", "Token recibidio: ${authResponse.token}")

                tokenManager.saveToken(authResponse.token)

                _navigateToProjects.value = true
            } catch (e: Exception) {
                val errorMessage = if (e.message?.contains("401") == true) {
                    "Credenciales incorrectas. Por favor verifica tu usuario y contraseña."
                } else {
                    e.message ?: "Error desconocido"
                }
                _uiState.update { it.copy(error = errorMessage) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNavigationDone() {
        _navigateToProjects.value = false
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
                val isUserNotFound = if (e is retrofit2.HttpException) {
                    e.code() == 404
                } else {
                    e.message?.contains("USER_NOT_FOUND") == true || e.message?.contains("404") == true
                }

                if (isUserNotFound) {
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