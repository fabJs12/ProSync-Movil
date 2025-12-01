package com.luna.prosync.ui.screens.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val showUsernameDialog: Boolean = false,
    val googleToken: String? = null
)
