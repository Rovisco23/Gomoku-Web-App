package pt.isel.daw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.daw.domainmodel.game.Board
import pt.isel.daw.domainmodel.game.Game
import pt.isel.daw.domainmodel.game.GameState
import pt.isel.daw.domainmodel.game.OpeningRule
import java.sql.ResultSet
import java.util.*

class GameMapper : RowMapper<Game> {
    override fun map(rs: ResultSet?, ctx: StatementContext?) = if (rs != null) {
        Game(
            UUID.fromString(rs.getString("id")),
            Board.fromString(rs.getString("board")),
            GameState.fromString(rs.getString("game_state")),
            OpeningRule.fromString(rs.getString("rule_set")),
            rs.getInt("player_b"),
            rs.getInt("player_w")
        )
    } else null
}