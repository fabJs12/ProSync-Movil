package com.luna.prosync.data.repository

import com.luna.prosync.data.remote.ApiService
import com.luna.prosync.data.remote.dto.CommentDto
import com.luna.prosync.data.remote.dto.CreateCommentRequest
import com.luna.prosync.data.remote.dto.CreateTaskRequest
import com.luna.prosync.data.remote.dto.TaskDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMyTasks(): List<TaskDto> {
        return apiService.getMyTasks()
    }

    suspend fun createTask(
        boardId: Int,
        estadoId: Int,
        title: String,
        description: String?
    ): TaskDto {
        val request = CreateTaskRequest(
            title = title,
            description = description,
            boardId = boardId,
            estadoId = estadoId,
            dueDate = null,
            responsableId = null

        )

        return apiService.createTask(request)
    }

    suspend fun getComments(taskId: Int): List<CommentDto> {
        return apiService.getTaskComments(taskId)
    }

    suspend fun addComment(taskId: Int, userId: Int, contenido: String): CommentDto {
        val request = CreateCommentRequest(taskId, userId, contenido)
        return apiService.createComment(request)
    }
}