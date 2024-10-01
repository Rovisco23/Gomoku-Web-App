package pt.isel.daw.http.model

data class UserInputModel(val name: String, val email: String, val password: String)

data class UserLoginInputModel(val username: String, val password: String)

data class UserOutputModel(val id: Int, val username: String, val email: String)

data class TokenOutputModel(val userId: Int, val username: String, val token: String)

data class RankingOutputModel(
    val id: Int,
    val games_played: Int,
    val games_won: Int,
    val games_lost: Int,
    val games_drawn: Int,
    val win_rate: String
)

data class AboutOutputModel(
    val authors: String = "49508 - João Mota, 49487 - Ricardo Rovisco, 48337 - Daniel Antunes",
    val version: String = "1.0.2"
)