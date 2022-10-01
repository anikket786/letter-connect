package com.example.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Move(
    val from: Int,
    val to: Int,
)