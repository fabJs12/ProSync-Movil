package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProjectDetailDto(
    val id: Int,
    val name: String,
    val description: String?,
    val miembros: List<MemberDto>
)

@Serializable
data class MemberDto(
    val userId: Int,
    val username: String,
    val email: String,
    val rol: String
)
