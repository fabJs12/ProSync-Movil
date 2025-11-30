package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateProjectRequest(
    val name: String,
    val description: String?
)
