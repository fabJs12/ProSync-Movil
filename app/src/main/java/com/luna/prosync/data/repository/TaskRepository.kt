package com.luna.prosync.data.repository

import com.luna.prosync.data.remote.ApiService
import com.luna.prosync.data.remote.dto.CommentDto
import com.luna.prosync.data.remote.dto.CreateCommentRequest
import com.luna.prosync.data.remote.dto.CreateTaskRequest
import com.luna.prosync.data.remote.dto.TaskDto
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
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

    suspend fun getTaskFiles(taskId: Int): List<com.luna.prosync.data.remote.dto.TaskFileDto> {
        return apiService.getTaskFiles(taskId)
    }

    suspend fun uploadFile(taskId: Int, file: File, mimeType: String): com.luna.prosync.data.remote.dto.TaskFileDto? {
        val requestFile = RequestBody.create(mimeType.toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val response = apiService.uploadFile(taskId, body)
        return if (response.isSuccessful) response.body() else null
    }
}