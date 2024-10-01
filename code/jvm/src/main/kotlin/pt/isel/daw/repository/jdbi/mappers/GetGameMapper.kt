package pt.isel.daw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.daw.domainmodel.game.Board
import pt.isel.daw.http.model.GetGameModel
import java.sql.ResultSet
import java.util.*


class GetGameMapper : RowMapper<GetGameModel> {
    override fun map(rs: ResultSet?, ctx: StatementContext?) = if (rs != null) {
        GetGameModel(
                UUID.fromString(rs.getString("id")),
                Board.fromString(rs.getString("board")).toExternal(),
                rs.getString("game_state"),
                rs.getString("rule_set"),
                rs.getInt("player_b"),
                rs.getString("black_name"),
                rs.getDouble("bWinRate"),
                rs.getInt("player_w"),
                rs.getString("white_name"),
                rs.getDouble("wWinRate"),
                rs.getInt("winner")
        )
    } else null
}