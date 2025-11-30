package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: Int,
    val mensaje: String,
    val tipo: String? = null,
    val leida: Boolean,
    val createdAt: String? = null,
)
