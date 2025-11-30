package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Int,
    val title: String,
    val description: String? = null,
    val estado: EstadoDto? = null,
    val dueDate: String? = null,
    val createdAt: String? = null,
    val boardId: Int? = null,
    val boardName: String? = null,
    val estadoId: Int? = null,
    val estadoNombre: String? = null,
    val responsableId: Int? = null,
    val responsableUsername: String? = null,
    val projectId: Int? = null,
    val projectName: String? = null
)

@Serializable
data class EstadoDto(
    val id: Int,
    val estado: String
)
