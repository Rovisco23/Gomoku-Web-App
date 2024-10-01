package pt.isel.daw.domainmodel.user

import pt.isel.daw.http.model.RankingOutputModel

data class Ranking(
    val userId: Int,
    val username: String,
    val games_played: Int,
    val games_won: Int,
    val games_drawn: Int,
    val win_rate: Double
)

fun Ranking.toOutputModel() = RankingOutputModel(
    userId,
    games_played,
    games_won,
    games_played - games_won - games_drawn,
    games_drawn,
    String.format("%.2f", win_rate * 100) + "%"
)
