package com.luna.prosync.ui.screens.home

import com.luna.prosync.data.remote.dto.DashboardStatsDto
import com.luna.prosync.data.remote.dto.UserDto

data class HomeUiState(
    val user: UserDto? = null,
    val stats: DashboardStatsDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
