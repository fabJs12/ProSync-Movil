package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskFileDto(
    val id: Int,
    val archivoUrl: String,
    val createdAt: String? = null
)
