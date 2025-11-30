package com.luna.prosync.ui.screens.project_detail

import com.luna.prosync.data.remote.dto.BoardDto
import com.luna.prosync.data.remote.dto.TaskDto

data class ProjectDetailUiState(
    val projectId: Int = 0,
    val selectedBoard: BoardDto? = null,
    val tasks: List<TaskDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
