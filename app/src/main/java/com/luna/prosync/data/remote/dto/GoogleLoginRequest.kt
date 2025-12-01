package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleLoginRequest(
    val token: String,
    val username: String? = null
)
