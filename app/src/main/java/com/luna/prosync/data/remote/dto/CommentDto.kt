package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: Int,
    val contenido: String,
    val createdAt: String? = null,
    val user: UserDto? = null
)


