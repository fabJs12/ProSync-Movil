package com.luna.prosync.ui.screens.projects

import com.luna.prosync.data.remote.dto.ProjectDto

data class ProjectUiState(
    val projects: List<ProjectDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
