package pt.isel.daw.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.repository.GamesRepository
import pt.isel.daw.repository.LobbiesRepository
import pt.isel.daw.repository.TokensRepository
import pt.isel.daw.repository.Transaction
import pt.isel.daw.repository.UsersRepository

class JdbiTransaction(
    private val handle: Handle

) : Transaction {

    override val usersRepository: UsersRepository = JdbiUsersRepository(handle)
    override val gamesRepository: GamesRepository = JdbiGamesRepository(handle)
    override val lobbiesRepository: LobbiesRepository = JdbiLobbiesRepository(handle)
    override val tokensRepository: TokensRepository = JdbiTokensRepository(handle)

    override fun rollback() {
        handle.rollback()
    }
}