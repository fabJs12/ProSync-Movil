package com.luna.prosync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateBoardRequest(
    val name: String
)
