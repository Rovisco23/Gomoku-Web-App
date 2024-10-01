package pt.isel.daw.repository

import pt.isel.daw.domainmodel.game.Lobby
import pt.isel.daw.domainmodel.game.OpeningRule
import java.util.*

interface LobbiesRepository {
    fun createLobby(userId: Int, rule: OpeningRule, size: Int)
    fun getLobbyUpdate(userId: Int): String?
    fun getAvailableLobby(userId: Int, rule: OpeningRule, size: Int): Lobby?
    fun updateLobby(userId: Int, gameId: UUID)
    fun deleteLobby(userId: Int)
}