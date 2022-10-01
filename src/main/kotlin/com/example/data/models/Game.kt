package com.example.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Game(
    var winner: Player? = null,
    @SerialName("current_player")
    var currentPlayer: Player = Player.first,
    var nodes: List<Node>,
    @Transient
    var remainingConnections: Int = 0,
)
