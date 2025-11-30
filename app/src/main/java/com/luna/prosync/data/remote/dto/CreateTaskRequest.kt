package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String?,
    val dueDate: String? = null,
    val boardId: Int,
    val estadoId: Int,
    val responsableId: Int? = null,

)