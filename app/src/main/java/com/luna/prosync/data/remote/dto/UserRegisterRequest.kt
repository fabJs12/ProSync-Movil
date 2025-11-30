package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
