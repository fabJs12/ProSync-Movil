package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RolDto(
    val id: Int,
    val rol: String
)
