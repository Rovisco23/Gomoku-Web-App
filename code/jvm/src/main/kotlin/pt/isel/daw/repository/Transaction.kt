package pt.isel.daw.repository

interface Transaction {
    val usersRepository: UsersRepository
    val gamesRepository: GamesRepository
    val lobbiesRepository: LobbiesRepository
    val tokensRepository: TokensRepository
    fun rollback()
}

