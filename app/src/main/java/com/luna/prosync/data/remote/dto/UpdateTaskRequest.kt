package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTaskRequest(
    val title: String,
    val description: String?,
    val estado: EstadoDto,
    val dueDate: String?
)
