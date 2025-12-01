package com.luna.prosync.data.repository

import com.luna.prosync.data.remote.ApiService
import com.luna.prosync.data.remote.dto.DashboardStatsDto
import com.luna.prosync.data.remote.dto.InviteMemberRequest
import com.luna.prosync.data.remote.dto.NotificationDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getNotifications(): List<NotificationDto> {
        return apiService.getUserNotifications().content
    }

    suspend fun markNotificationAsRead(id: Int) {
        apiService.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        apiService.markAllNotificationsAsRead()
    }

    /* TODO: Not implemented in API
    suspend fun inviteMember(projectId: Int, email: String) {
        apiService.inviteMember(projectId, InviteMemberRequest(email))
    }

    suspend fun acceptInvitation(projectId: Int) {
        apiService.acceptInvitation(projectId)
    }
    */

    suspend fun getStats(): DashboardStatsDto {
        return apiService.getDashboardStats()
    }
}