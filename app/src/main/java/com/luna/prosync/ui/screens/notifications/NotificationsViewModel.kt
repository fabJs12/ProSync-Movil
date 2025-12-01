package com.luna.prosync.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.prosync.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val notifications = dashboardRepository.getNotifications()
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        notifications = notifications
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Error al cargar notificaciones: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            // Optimistic update
            _uiState.update { state ->
                state.copy(
                    notifications = state.notifications.map { 
                        if (it.id == notificationId) it.copy(leida = true) else it 
                    }
                )
            }
            try {
                dashboardRepository.markNotificationAsRead(notificationId)
            } catch (e: Exception) {
                // Revert on error (optional, or show message)
                // For now just log or ignore as optimistic update is preferred
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            // Optimistic update
            _uiState.update { state ->
                state.copy(
                    notifications = state.notifications.map { it.copy(leida = true) }
                )
            }
            try {
                dashboardRepository.markAllNotificationsAsRead()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
