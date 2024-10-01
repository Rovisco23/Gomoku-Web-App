package pt.isel.daw.http.siren

import pt.isel.daw.domainmodel.user.User
import pt.isel.daw.http.PathTemplate
import pt.isel.daw.http.model.*

object SirenMaker {

    fun sirenSignIn(body: User): SirenModel<User> {
        return siren(body) {
            klass("SignIn")
            link("/login", LinkRelation("/login"))
        }
    }

    fun sirenLogin(body: TokenOutputModel): SirenModel<TokenOutputModel> {
        return siren(body) {
            klass("Login")
            link(PathTemplate.Users.HOME, LinkRelation(PathTemplate.Users.HOME))
        }
    }

    fun sirenLogout(body: Boolean): SirenModel<Boolean> {
        return siren(body) {
            klass("Logout")
            link(PathTemplate.Users.HOME, LinkRelation(PathTemplate.Users.HOME))
        }
    }

    fun sirenGetUser(body: UserOutputModel): SirenModel<UserOutputModel> {
        return siren(body) {
            klass("GetUser")
        }
    }

    fun sirenRankings(body: Any): SirenModel<Any> {
        return siren(body) {
            klass("Rankings")
        }
    }

    fun sirenAbout(body: AboutOutputModel): SirenModel<AboutOutputModel> {
        return siren(body) {
            klass("About")
        }
    }

    fun sirenGetGame(body: GetGameModel): SirenModel<GetGameModel> {
        return siren(body) {
            klass("GetGame")
        }
    }

    fun sirenPlay(body: GameExternalValues): SirenModel<GameExternalValues> {
        return siren(body) {
            klass("Play")
        }
    }

    fun sirenStartGame(body: String): SirenModel<Any> {
        return siren(body) {
            klass("StartGame")
            link(PathTemplate.Games.LOBBY, LinkRelation(PathTemplate.Games.LOBBY))
        }
    }


    fun sirenCheckLobbyUpdate(body: String): SirenModel<String> {
        return siren(body) {
            klass("CheckLobbyUpdate")
        }
    }

    fun sirenGiveUpGame(body: GameExternalValues) : SirenModel<GameExternalValues> {
        return siren(body) {
            klass("Give Up")
        }
    }
}