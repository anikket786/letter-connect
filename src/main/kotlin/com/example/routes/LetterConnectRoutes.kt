package com.example.routes

import com.example.controller.LetterConnectController
import com.example.core.exceptions.GameNotFoundException
import com.example.core.exceptions.GameOverException
import com.example.core.exceptions.InvalidMoveException
import com.example.core.exceptions.MissingGameIdException
import com.example.data.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.Exception
import java.lang.IllegalArgumentException

fun Route.letterConnectRouting() {
    val letterConnectController = LetterConnectController()

    route("/games") {
        // Creates a new game
        post {
            try {
                val nodeLayout = call.receive<NodeLayout>()
                val response = letterConnectController.createNewGame(nodeLayout.nodes)
                call.respond(response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("{id?}") {
            try {
                val gameId = call.parameters["id"]
                val game = letterConnectController.getGameById(gameId)
                call.respond(game)
            } catch (e: Exception) {
                call.respondText(e.message ?: "", status = HttpStatusCode.NotFound)
            }
        }

        post("{id}/move") {
            try {
                val gameId = call.parameters["id"]
                val currentGame = letterConnectController.validateGameStatus(gameId)

                val move = call.receive<Move>()
                val updatedGame = letterConnectController.makeMove(currentGame, move)
                call.respond(updatedGame)
            } catch (e: MissingGameIdException) {
                call.respondText("Missing id", status = HttpStatusCode.BadRequest)
            } catch (e: GameNotFoundException) {
                call.respondText("Game not found", status = HttpStatusCode.BadRequest)
            } catch (e: GameOverException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    GameMoveErrorResponse(GameMoveError.game_is_over)
                )
            } catch (e: InvalidMoveException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    GameMoveErrorResponse(GameMoveError.invalid_move)
                )
            }
        }
    }
}