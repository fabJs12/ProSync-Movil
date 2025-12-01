package com.luna.prosync.ui.screens.register

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val showUsernameDialog: Boolean = false,
    val googleToken: String? = null
)
