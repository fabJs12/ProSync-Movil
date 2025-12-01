package com.luna.prosync.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.prosync.data.local.TokenManager
import com.luna.prosync.data.repository.AuthRepository
import com.luna.prosync.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dashboardRepository: DashboardRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            // Smart loading: only show loading if we don't have data yet
            if (_uiState.value.stats == null) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            
            try {
                coroutineScope {
                    val userDeferred = async { authRepository.getProfile() }
                    val statsDeferred = async { dashboardRepository.getStats() }
                    val notificationsDeferred = async { dashboardRepository.getNotifications() }

                    val user = userDeferred.await()
                    val stats = statsDeferred.await()
                    val notifications = notificationsDeferred.await()
                    val unreadCount = notifications.count { !it.leida }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            stats = stats,
                            unreadNotificationsCount = unreadCount
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al cargar datos ${e.message}")
                }
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            tokenManager.clearToken()
        }
    }
}