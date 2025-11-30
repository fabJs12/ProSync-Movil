package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
    val taskId: Int,
    val userId: Int,
    val contenido: String
)