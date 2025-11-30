package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BoardDto(
    val id: Int,
    val name: String
)
