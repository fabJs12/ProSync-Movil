package com.luna.prosync.data.repository

import com.luna.prosync.data.remote.ApiService
import com.luna.prosync.data.remote.dto.CreateProjectRequest
import com.luna.prosync.data.remote.dto.ProjectDto
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProjects(): List<ProjectDto> {
        return apiService.getProjects()
    }

    suspend fun createProject(name: String, description: String?): ProjectDto {
        val request = CreateProjectRequest(name = name, description = description)
        return apiService.createProject(request)
    }
}