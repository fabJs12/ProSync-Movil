package com.luna.prosync.ui.screens.task_detail

import com.luna.prosync.data.remote.dto.CommentDto
import com.luna.prosync.data.remote.dto.TaskDto
import com.luna.prosync.data.remote.dto.UserProjectDto

data class TaskDetailUiState(
    val task: TaskDto? = null,
    val members: List<UserProjectDto> = emptyList(),
    val comments: List<CommentDto> = emptyList(),
    val currentUserId: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val isLeader: Boolean = false,
    val attachments: List<com.luna.prosync.data.remote.dto.TaskFileDto> = emptyList()
)
