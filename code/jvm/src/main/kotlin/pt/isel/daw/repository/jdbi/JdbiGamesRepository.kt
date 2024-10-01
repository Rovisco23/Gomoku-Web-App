package pt.isel.daw.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.domainmodel.game.*
import pt.isel.daw.repository.GamesRepository
import java.util.*
import pt.isel.daw.http.model.GetGameModel

class JdbiGamesRepository(private val handle: Handle) : GamesRepository {
    override fun getById(id: UUID) = handle.createQuery(
            "SELECT g.id, g.board, g.game_state, g.rule_set, g.player_b, u_b.username AS black_name, " +
                    "COALESCE(r_b.games_won * 100.0 / NULLIF(r_b.games_played, 0), 0) AS bWinRate, g.player_w, " +
                    "u_w.username AS white_name, COALESCE(r_w.games_won * 100.0 / NULLIF(r_w.games_played, 0), 0) " +
                    "AS wWinRate, g.winner FROM games g LEFT JOIN rankings r_b ON r_b.user_id = g.player_b LEFT JOIN " +
                    "users u_b ON u_b.id = r_b.user_id LEFT JOIN rankings r_w ON r_w.user_id = g.player_w LEFT JOIN " +
                    "users u_w ON u_w.id = r_w.user_id WHERE g.id = :id;"
    )
            .bind("id", id.toString())
            .mapTo(GetGameModel::class.java)
            .singleOrNull()

    override fun update(game: Game) {
        handle.createUpdate("update games set board=:board where id=:id")
                .bind("id", game.id.toString())
                .bind("board", game.board.toString())
                .execute()
    }

    override fun checkGame(userId: Int): String? = handle.createQuery(
            "select id from games where (player_b = :userId or player_w = :userId) and" +
                    " (game_state = 'Starting' or game_state = 'On Going')"
    )
            .bind("userId", userId)
            .mapTo(String::class.java)
            .singleOrNull()

    override fun win(game: Game, winner: Int) {
        handle.createUpdate("update games set board=:board, game_state=:game_state, winner=:winner where id=:id")
                .bind("board", game.board.toString())
                .bind("game_state", game.state.toString())
                .bind("winner", winner)
                .bind("id", game.id.toString())
                .execute()
    }

    override fun draw(game: Game) {
        handle.createUpdate("update games set board=:board, game_state=:game_state where id=:id")
            .bind("board", game.board.toString())
            .bind("game_state", game.state.toString())
            .bind("id", game.id.toString())
            .execute()
    }

    override fun createGame(game: Game) {
        handle.createUpdate(
                "insert into games(id, board, game_state, rule_set, player_b, player_w) " +
                        "values (:id, :board, :game_state, :rule_set, :player_b , :player_w)"
        )
                .bind("id", game.id)
                .bind("board", game.board.toString())
                .bind("game_state", game.state.toString())
                .bind("rule_set", game.openingRule.toString())
                .bind("player_b", game.playerB)
                .bind("player_w", game.playerW)
                .execute()
    }

    override fun checkGameExists(id: Int) =
            handle.createQuery("select count(*) from games where id = :id")
                    .bind("id", id)
                    .mapTo(Int::class.java)
                    .single() == 1

    override fun getInvalidMoves(currMove: Int, size: Int, openingRule: OpeningRule) = handle.createQuery(
            "select next_turn, moves from invalid_moves where currMove = :move and boardSize = :size " +
                    "and rule_set = :rule_set"
    )
            .bind("move", currMove)
            .bind("size", size)
            .bind("rule_set", openingRule.toString())
            .mapTo(InvalidMoves::class.java)
            .single().invalidMoves

    override fun updateGameState(id: UUID, state: GameState) {
        handle.createUpdate(
                "update games set game_state = :game_state where id = :id"
        )
                .bind("game_state", state.toString())
                .bind("id", id.toString())
                .execute()
    }

    override fun delete(gameId: UUID) {
        handle.createUpdate("delete from games where id = :id")
                .bind("id", gameId.toString())
                .execute()
    }
}