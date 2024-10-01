package pt.isel.daw.http

object PathTemplate {

    const val PREFIX = "/api"

    object Users {
        const val HOME = "/"
        const val USER_BY_ID = "$PREFIX/users/{id}"
        const val SIGN_IN = "$PREFIX/signin"
        const val LOGIN = "$PREFIX/login"
        const val LOGOUT = "$PREFIX/logout"
        const val RANKINGS = "$PREFIX/rankings"
        const val USER_RANKING = "$PREFIX/rankings/{id}"
        const val ABOUT = "$PREFIX/about"
        const val CHECK_SESSION = "$PREFIX/check"
    }

    object Games {
        const val START = "$PREFIX/games"
        const val GAME_BY_ID = "$PREFIX/games/{id}"
        const val PLAY = "$PREFIX/games/{id}"
        const val DRAW = "$PREFIX/giveup/{id}"
        const val LOBBY = "/lobby"
        const val LOBBY_UPDATE = "$PREFIX/lobbies/{userId}"
        const val DELETE = "$PREFIX/games/{id}"
    }
}