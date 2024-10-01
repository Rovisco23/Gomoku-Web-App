package pt.isel.daw.domainmodel.game

import pt.isel.daw.repository.GamesRepository
import pt.isel.daw.repository.LobbiesRepository
import pt.isel.daw.domainmodel.game.OpeningRule.FREE_STYLE
import java.util.*

data class Lobby(val userId: Int, val rule: OpeningRule, val size: Int) {

    companion object {

        fun create(lRep: LobbiesRepository, gRep: GamesRepository, userId: Int, rule: OpeningRule, size: Int): String? {
            val availableLobby = lRep.getAvailableLobby(userId, rule, size)
            return if (availableLobby != null) {
                val gameId = UUID.randomUUID()
                lRep.updateLobby(availableLobby.userId, gameId)
                val newGame = Game(
                    gameId,
                    Board.create(availableLobby.size),
                    if (rule != FREE_STYLE) GameState.STARTING else GameState.ON_GOING,
                    rule,
                    availableLobby.userId,
                    userId
                )
                gRep.createGame(newGame)
                newGame.id.toString()
            } else {
                lRep.createLobby(userId, rule, size)
                null
            }
        }

        fun checkUpdate(lobbyRep: LobbiesRepository, userId: Int): String? {
            return when (val gameId = lobbyRep.getLobbyUpdate(userId)) {
                "" -> "Lobby is not full"
                null -> null
                else -> {
                    lobbyRep.deleteLobby(userId)
                    gameId
                }
            }
        }
    }
}

data class LobbyOutputModel(val userId: Int, val rule: String, val size: Int)

fun Lobby.toLobbyOutputModel() = LobbyOutputModel(userId, rule.toString(), size)