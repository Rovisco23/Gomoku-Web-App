package pt.isel.daw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.daw.domainmodel.game.InvalidMoves
import java.sql.ResultSet

class InvalidMovesMapper : RowMapper<InvalidMoves> {
    override fun map(rs: ResultSet?, ctx: StatementContext?) = if (rs != null) {
        InvalidMoves(
            rs.getString("next_turn").single(),
            rs.getString("moves")
        )
    } else null
}