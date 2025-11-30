package com.luna.prosync.ui.screens.create_task

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
