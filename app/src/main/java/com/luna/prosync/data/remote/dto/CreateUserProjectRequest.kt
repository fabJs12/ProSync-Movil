package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserProjectRequest(
    val userId: Int,
    val projectId: Int,
    val rolId: Int
)
