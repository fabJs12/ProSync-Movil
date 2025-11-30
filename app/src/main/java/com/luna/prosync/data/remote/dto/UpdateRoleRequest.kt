package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRoleRequest(
    val rolId: Int
)