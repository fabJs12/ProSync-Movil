package com.luna.prosync.ui.screens.my_tasks

import com.luna.prosync.data.remote.dto.TaskDto

data class MyTasksUiState(
    val tasks: List<TaskDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
