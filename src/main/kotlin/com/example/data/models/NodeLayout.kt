package com.example.data.models

import kotlinx.serialization.Serializable

@Serializable
data class NodeLayout(
    val nodes: List<NodeType>
)