package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProjectDto(
    val usuario: UserDto,
    val rol: RolDto
)
