package com.luna.prosync.data.repository

import com.luna.prosync.data.remote.ApiService
import com.luna.prosync.data.remote.dto.BoardDto
import com.luna.prosync.data.remote.dto.CreateBoardRequest
import com.luna.prosync.data.remote.dto.CreateTaskRequest
import com.luna.prosync.data.remote.dto.EstadoDto
import com.luna.prosync.data.remote.dto.TaskDto
import com.luna.prosync.data.remote.dto.UpdateTaskRequest
import com.luna.prosync.data.remote.dto.UserProjectDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getBoards(projectId: Int): List<BoardDto> {
        return apiService.getBoardsByProject(projectId)
    }

    suspend fun getTasks(boardId: Int): List<TaskDto> {
        return apiService.getTasksByBoard(boardId)
    }

    suspend fun createBoard(projectId: Int, nombre: String): BoardDto {
        val request = CreateBoardRequest(name = nombre)
        return apiService.createBoard(projectId, request)
    }

    suspend fun getTaskById(taskId: Int): TaskDto {
        return apiService.getTaskById(taskId)
    }

    suspend fun updateTask(taskId: Int, title: String, description: String?, statusId: Int, dueDate: String?): TaskDto {
        val estadoName = when(statusId) {
            1 -> "Pendiente"
            2 -> "En Progreso"
            3 -> "Hecho"
            else -> "Pendiente"
        }
        val estadoDto = EstadoDto(id = statusId, estado = estadoName)

        val request = UpdateTaskRequest(
            title = title,
            description = description,
            estado = estadoDto,
            dueDate = dueDate
        )
        return apiService.updateTask(taskId, request)
    }

    suspend fun getProjectMembers(projectId: Int): List<UserProjectDto> {
        return apiService.getProjectMembers(projectId)
    }
}