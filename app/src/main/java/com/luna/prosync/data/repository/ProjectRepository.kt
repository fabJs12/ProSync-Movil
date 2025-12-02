package com.luna.prosync.data.repository

import com.luna.prosync.data.remote.ApiService
import com.luna.prosync.data.remote.dto.CreateProjectRequest
import com.luna.prosync.data.remote.dto.ProjectDto
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ProjectRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProjects(): List<ProjectDto> {
        return apiService.getProjects()
    }

    suspend fun getProjectsWithStats(): List<ProjectDto> = coroutineScope {
        val projects = apiService.getProjects()
        
        projects.map { project ->
            async {
                try {
                    // Fetch details for members
                    val details = apiService.getProjectDetails(project.id)
                    val memberCount = details.miembros.size

                    // Fetch boards and tasks for task count
                    val boards = apiService.getBoardsByProject(project.id)
                    val taskCount = boards.map { board ->
                        async {
                            try {
                                apiService.getTasksByBoard(board.id).size
                            } catch (e: Exception) {
                                0
                            }
                        }
                    }.awaitAll().sum()

                    project.copy(miembros = memberCount, tareas = taskCount)
                } catch (e: Exception) {
                    project
                }
            }
        }.awaitAll()
    }

    suspend fun createProject(name: String, description: String?): ProjectDto {
        val request = CreateProjectRequest(name = name, description = description)
        return apiService.createProject(request)
    }
}