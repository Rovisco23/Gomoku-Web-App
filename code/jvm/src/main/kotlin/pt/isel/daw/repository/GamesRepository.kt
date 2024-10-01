package pt.isel.daw.repository

import pt.isel.daw.domainmodel.game.Game
import pt.isel.daw.domainmodel.game.GameState
import pt.isel.daw.domainmodel.game.OpeningRule
import pt.isel.daw.http.model.GetGameModel
import java.util.*

interface GamesRepository {
    fun getById(id: UUID): GetGameModel?
    fun createGame(game: Game)
    fun update(game: Game)
    fun win(game: Game, winner: Int)
    fun draw(game: Game)
    fun delete(gameId: UUID)
    fun checkGameExists(id: Int): Boolean
    fun getInvalidMoves(currMove: Int, size: Int, openingRule: OpeningRule): Pair<Char, List<Pair<Int, Char>>>
    fun checkGame(userId: Int): String?
    fun updateGameState(id: UUID, state: GameState)
}