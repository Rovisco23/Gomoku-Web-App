package pt.isel.daw.service

import org.springframework.stereotype.Component
import pt.isel.daw.domainmodel.game.*
import pt.isel.daw.http.model.Errors
import pt.isel.daw.http.model.GetGameModel
import pt.isel.daw.http.utils.Result
import pt.isel.daw.http.utils.failure
import pt.isel.daw.http.utils.success
import pt.isel.daw.repository.GamesRepository
import pt.isel.daw.repository.TransactionManager
import java.util.*

typealias GameResult = Result<Errors, Game>
typealias GetGameResult = Result<Errors, GetGameModel>
typealias GameCreateResult = Result<Errors, String>
typealias LobbyUpdateResult = Result<Errors, String>

@Component
class GamesService(
    private val transactionManager: TransactionManager
) {
    fun play(id: UUID, loggedUserId: Int, userId: Int, row: Int, col: Char): GameResult = transactionManager.run {
        val gridValues = paramsToGridValues(row, col)
        val gameRep = it.gamesRepository
        val game = it.gamesRepository.getById(id)?.toGame()
        val checkedMove = game?.checkMove(gameRep, row, col, userId)
        when {
            game == null -> failure(Errors.gameNotFound)
            userId != loggedUserId -> failure(Errors.unauthorizedPlayer)
            game.isFinished() -> failure(Errors.gameFinished)
            game.playerB != userId && game.playerW != userId -> failure(Errors.unauthorizedPlayer)
            game.checkWrongTurn(userId) -> failure(Errors.wrongTurn)
            !game.board.checkIfEmpty(gridValues.first, gridValues.second) -> failure(Errors.cellOccupied)
            checkedMove != null && !checkedMove.second -> failure(Errors.invalidMove)
            game.isPlayableTurn(userId) -> {
                val currTurn = game.getTurnCredentials(userId)
                val nextTurn = checkedMove?.first ?: currTurn.getNextTurn()
                success(game.processTurn(gameRep, gridValues.first, gridValues.second, userId, currTurn, nextTurn))
            }

            else -> failure(Errors.internalError)
        }
    }

    fun start(loggedUserId: Int, userId: Int, openingRule: OpeningRule, boardSize: Int): GameCreateResult =
        transactionManager.run {
            val gameRep = it.gamesRepository
            val lobbyRep = it.lobbiesRepository
            val userRep = it.usersRepository
            val game = gameRep.checkGame(userId)
            if (game == null) lobbyRep.deleteLobby(userId)
            when {
                userId != loggedUserId -> failure(Errors.invalidParameter)
                !userRep.checkUserExist(userId) -> failure(Errors.invalidParameter)
                !OpeningRule.checkRule(openingRule) -> failure(Errors.invalidParameter)
                !Board.checkSize(boardSize) -> failure(Errors.invalidParameter)
                game != null -> success(game)
                else -> {
                    val newGame = Lobby.create(lobbyRep, gameRep, userId, openingRule, boardSize)
                    success(newGame ?: "Lobby Created")
                }
            }
        }

    fun getGame(id: UUID, userId: Int): GetGameResult = transactionManager.run {
        val game = it.gamesRepository.getById(id)
        if (game == null) {
            failure(Errors.gameNotFound)
        } else if (game.playerB != userId && game.playerW != userId) {
            failure(Errors.unauthorizedPlayer)
        } else {
            success(game)
        }
    }

    fun giveUpGame(id: UUID, userId: Int): GameResult = transactionManager.run {
        val game = it.gamesRepository.getById(id)?.toGame()
        if (game == null) {
            failure(Errors.gameNotFound)
        } else if (game.playerB != userId && game.playerW != userId) {
            failure(Errors.unauthorizedPlayer)
        } else {
            success(game.giveUp(it.gamesRepository, if(userId == game.playerW) game.playerB else game.playerW))
        }
    }

    fun deleteGame(id: UUID) {
        transactionManager.run { it.gamesRepository.delete(id) }
    }

    fun checkLobbyUpdate(userId: Int, loggedUserId: Int): LobbyUpdateResult = transactionManager.run {
        val lobbyRep = it.lobbiesRepository
        val lobby = Lobby.checkUpdate(lobbyRep, userId)
        if (lobby == null) {
            failure(Errors.lobbyNotFound)
        } else if (userId != loggedUserId) {
            failure(Errors.unauthorizedPlayer)
        } else {
            success(lobby)
        }
    }
}

private fun paramsToGridValues(row: Int, col: Char) = Pair(row - 1, col.lowercaseChar() - 'A' - 32)

private fun Game.checkMove(gRep: GamesRepository, row: Int, col: Char, userId: Int): Pair<Char, Boolean>? {
    val currTurn = getTurnCredentials(userId)
    var checkedMove: Pair<Char, Boolean>? = null
    if (state == GameState.STARTING) checkedMove = checkMove(gRep, row, col, currTurn)
    return checkedMove
}
