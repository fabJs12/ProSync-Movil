package com.luna.prosync.ui.screens.team

import com.luna.prosync.data.remote.dto.UserProjectDto

data class TeamUiState(
    val members: List<UserProjectDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
