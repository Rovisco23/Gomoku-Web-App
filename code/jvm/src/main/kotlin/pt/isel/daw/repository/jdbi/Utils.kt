package pt.isel.daw.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.daw.repository.jdbi.mappers.InstantMapper
import pt.isel.daw.repository.jdbi.mappers.TokenValidationInfoMapper
import pt.isel.daw.repository.jdbi.mappers.GameMapper
import pt.isel.daw.repository.jdbi.mappers.GetGameMapper
import pt.isel.daw.repository.jdbi.mappers.InvalidMovesMapper
import pt.isel.daw.repository.jdbi.mappers.LobbyMapper

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(TokenValidationInfoMapper())
    registerColumnMapper(InstantMapper())
    registerRowMapper(GameMapper())
    registerRowMapper(GetGameMapper())
    registerRowMapper(LobbyMapper())
    registerRowMapper(InvalidMovesMapper())

    return this
}