package pt.isel.daw.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.domainmodel.game.Lobby
import pt.isel.daw.domainmodel.game.OpeningRule
import pt.isel.daw.repository.LobbiesRepository
import java.util.*

class JdbiLobbiesRepository(private val handle: Handle) : LobbiesRepository {
    override fun createLobby(userId: Int, rule: OpeningRule, size: Int) {
        handle.createUpdate(
            "insert into lobbies (user_id, rule_set, boardSize, status, game_id) " +
                    "values (:user, :rule, :size, 'Not Full', '');"
        )
            .bind("user", userId)
            .bind("rule", rule.toString())
            .bind("size", size)
            .execute()
    }

    override fun getLobbyUpdate(userId: Int) = handle.createQuery(
        "select game_id from lobbies where user_id = :user;"
    )
        .bind("user", userId)
        .mapTo(String::class.java)
        .singleOrNull()


    override fun getAvailableLobby(userId: Int, rule: OpeningRule, size: Int) = handle.createQuery(
        "select user_id, rule_set, boardSize from lobbies where " +
                "(user_id != :user) and " +
                "(rule_set = :rule) and " +
                "(boardSize = :size) and " +
                "(status = 'Not Full');"
    )
        .bind("user", userId)
        .bind("rule", rule.toString())
        .bind("size", size)
        .mapTo(Lobby::class.java)
        .singleOrNull()


    override fun updateLobby(userId: Int, gameId: UUID) {
        handle.createUpdate("update lobbies set status = 'Full', game_id = :game where user_id = :user;")
            .bind("game", gameId.toString())
            .bind("user", userId)
            .execute()
    }

    override fun deleteLobby(userId: Int) {
        handle.createUpdate("delete from lobbies where user_id = :user;")
            .bind("user", userId)
            .execute()
    }

}