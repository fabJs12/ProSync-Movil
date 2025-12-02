package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProjectDto(
    val id: Int,
    val name: String,
    val description: String?,
    val miembros: Int = 0,
    val tareas: Int = 0
)
