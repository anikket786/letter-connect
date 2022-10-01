package com.example.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameMoveErrorResponse(
    @SerialName("error_type")
    val errorType: GameMoveError,
)