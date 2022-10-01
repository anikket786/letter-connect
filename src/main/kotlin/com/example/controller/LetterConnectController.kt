package com.example.controller

import com.example.core.exceptions.GameNotFoundException
import com.example.core.exceptions.GameOverException
import com.example.core.exceptions.InvalidMoveException
import com.example.core.exceptions.MissingGameIdException
import com.example.data.models.*
import com.example.data.storage.gameStorage
import java.util.*

class LetterConnectController {

    fun createNewGame(nodes: List<NodeType>) : CreateGameResponse {
        validateCreateGameRequest(nodes)

        // Map list of NodeTypes into list of Nodes and new Game object.
        var nodeId = 1
        val newGame = Game(
            nodes = nodes.map {
                return@map Node(id = nodeId++, type = it)
            },
        )

        val maxConnections = getMaxPossibleConnections(nodes)
        newGame.remainingConnections = maxConnections

        // If all node types are same then there is 0 possible connection between nodes
        // In this case the first player has no valid moves and hence second player must be the winner
        if (maxConnections == 0) {
            newGame.winner = Player.second
        }

        // Persist newly created game into memory
        val newGameId = gameStorage.size.toString()
        gameStorage[newGameId] = newGame

        return CreateGameResponse(gameId = newGameId)
    }

    private fun validateCreateGameRequest(nodes: List<NodeType>) {
        // Check if the request contains invalid number of nodes
        if (nodes.size < 3 || nodes.size > 50) {
            throw IllegalArgumentException(
                "Number of nodes must be greater than 2 and less than equals to 50",
            )
        }
    }

    private fun getMaxPossibleConnections(nodes: List<NodeType>): Int {
        val freqList = mutableListOf(0, 0, 0)
        for ((index, node) in nodes.distinct().withIndex()) {
            val freq = Collections.frequency(nodes, node)
            freqList[index] = freq
        }
        freqList.sort()
        val leastNodeCount = freqList[0]
        val secondLeastNodeCount = freqList[1]
        return (leastNodeCount * 2) + secondLeastNodeCount
    }

    fun getGameById(gameId: String?): Game {
        if (gameId == null) {
            throw Exception("Missing id")
        }
        return gameStorage[gameId] ?: throw Exception("Game not found")
    }

    fun validateGameStatus(gameId: String?) : Game {
        gameId ?: throw MissingGameIdException()

        val game = gameStorage[gameId] ?: throw GameNotFoundException()

        if (game.winner != null) {
            throw GameOverException()
        }

        return game
    }

    fun makeMove(game: Game, move: Move) : Game {
        // If from and to nodes are out of bound, then it is an illegal move
        if ((move.from < 1) || (move.to < 1)) {
            throw InvalidMoveException()
        }

        if ((move.from > game.nodes.size) || (move.to > game.nodes.size)) {
            throw InvalidMoveException()
        }

        val fromNode = game.nodes[move.from - 1]
        val toNode = game.nodes[move.to - 1]

        // If from and to NodeTypes are same, then it is an illegal move
        if (fromNode.type == toNode.type) {
            throw InvalidMoveException()
        }

        // If either one or both of from and to node have 2 connections,
        // then current move is an illegal move
        if ((fromNode.connections.size == 2) || (toNode.connections.size == 2)) {
            throw InvalidMoveException()
        }

        // If both nodes are already connected to each other, then it is an illegal move
        fromNode.connections.forEach { connectedTo ->
            if (connectedTo == move.to) {
                throw InvalidMoveException()
            }
        }

        // If to or from node is already connected to other node of same NodeType,
        // then it is an illegal move
        fromNode.connections.forEach { connectedTo ->
            val connectedNode = game.nodes[connectedTo - 1]
            if (connectedNode.type == toNode.type) {
                throw InvalidMoveException()
            }
        }

        toNode.connections.forEach { connectedTo ->
            val connectedNode = game.nodes[connectedTo - 1]
            if (connectedNode.type == fromNode.type) {
                throw java.lang.IllegalArgumentException()
            }
        }

        // If passed all checks, then connect to and from nodes together
        fromNode.connections.add(move.to)
        toNode.connections.add(move.from)

        // Decrement the remaining connections count
        game.remainingConnections--
        if (game.remainingConnections == 0) {
            game.winner = game.currentPlayer
        }

        // Switch player turn
        game.currentPlayer = if (game.currentPlayer == Player.first) {
            Player.second
        } else {
            Player.first
        }

        return  game
    }
}