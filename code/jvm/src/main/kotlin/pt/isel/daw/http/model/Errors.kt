package pt.isel.daw.http.model

import org.springframework.http.ResponseEntity

class Errors(val status: Int, val reason: String) {
    companion object {
        fun response(error: Errors) = ResponseEntity
            .status(error.status)
            .header("Content-Type", "application/problem+json")
            .body<Any>(error.reason)

        val userNotFound = Errors(404,"User does not exist.")

        val emailAlreadyInUse = Errors(400,"That email is already in use.")

        val cellOccupied = Errors(400,"That cell already has a piece.")

        val wrongTurn = Errors(400,"It's not your turn.")

        val invalidParameter = Errors(400,"Invalid Parameter.")

        val gameNotFound = Errors(404,"Game with given Id does not exist.")

        val gameFinished = Errors(400,"This game already ended.")

        val internalError = Errors(500,"Internal Server Error. Please try again later.")

        val lobbyNotFound = Errors(404,"No Lobby found.")

        val userOrPasswordAreInvalid = Errors(400,"User or password are invalid.")

        val invalidMove = Errors(400,"Move is not valid.")

        val invalidPassword = Errors(400,"Invalid Password.\nPassword must have at least 8 digits, one uppercase letter, one number and a symbol.")

        val unauthorizedPlayer = Errors(401,"You don't have access to this game.")

        val usernameAlreadyInUse = Errors(400,"That username is already in use.")
    }
}