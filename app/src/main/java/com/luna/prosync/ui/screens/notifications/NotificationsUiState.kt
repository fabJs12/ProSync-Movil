package com.luna.prosync.ui.screens.notifications

import com.luna.prosync.data.remote.dto.NotificationDto

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationDto> = emptyList(),
    val error: String? = null
)
