package pt.isel.daw.repository.jdbi.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.daw.domainmodel.game.Lobby
import pt.isel.daw.domainmodel.game.OpeningRule
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

class LobbyMapper : RowMapper<Lobby> {
    @Throws(SQLException::class)
    override fun map(rs: ResultSet?, ctx: StatementContext) = if (rs != null) {
        Lobby(
            rs.getInt("user_id"),
            OpeningRule.fromString(rs.getString("rule_set")),
            rs.getInt("boardSize")
        )
    } else null
}