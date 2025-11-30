package com.luna.prosync.ui.screens.create_project

data class CreateProjectUiState(
    val name: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
