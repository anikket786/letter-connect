package com.example.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Node(
    val id: Int,
    val type: NodeType,
    val connections: MutableList<Int> = mutableListOf(),
)
