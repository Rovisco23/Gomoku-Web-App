package pt.isel.daw.http

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.domainmodel.authentication.AuthenticatedUser
import pt.isel.daw.http.model.AboutOutputModel
import pt.isel.daw.http.model.UserLoginInputModel
import pt.isel.daw.http.model.TokenOutputModel
import pt.isel.daw.http.model.UserInputModel
import pt.isel.daw.http.siren.SirenMaker
import pt.isel.daw.http.utils.success
import pt.isel.daw.service.UsersService
import org.springframework.http.ResponseCookie
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import jakarta.servlet.http.HttpServletResponse
import pt.isel.daw.http.siren.response

@RestController
class UsersController(private val usersService: UsersService) {

    @GetMapping(PathTemplate.Users.USER_BY_ID)
    fun getUser(@PathVariable id: Int): ResponseEntity<*> {
        val res = usersService.getUser(id)
        return handleResponse(res) {
            val siren = SirenMaker.sirenGetUser(it)
            ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(siren)
        }
    }

    @PostMapping(PathTemplate.Users.SIGN_IN)
    fun createUser(@RequestBody u: UserInputModel): ResponseEntity<*> {
        val res = usersService.create(u.name, u.email, u.password)
        return handleResponse(res) {
            SirenMaker.sirenSignIn(it).response(201)
        }
    }

    @PostMapping(PathTemplate.Users.LOGIN)
    fun login(@RequestBody u: UserLoginInputModel, response: HttpServletResponse): ResponseEntity<*> {
        val res = usersService.login(u.username, u.password)
        return handleResponse(res) {
            val tokenValue = TokenOutputModel(
                    userId = it.userId,
                    username = it.username,
                    token = it.tokenValue
            )
            val siren = SirenMaker.sirenLogin(tokenValue)
            val cookieToken = ResponseCookie
                .from("token", tokenValue.token)
                .path("/")
                .maxAge(it.tokenExpiration.epochSeconds)
                .httpOnly(true)
                .secure(false)
                .build()

            val cookieId =  ResponseCookie
                .from("id", tokenValue.userId.toString())
                .path("/")
                .maxAge(it.tokenExpiration.epochSeconds)
                .httpOnly(true)
                .secure(false)
                .build()

            response.addHeader(HttpHeaders.SET_COOKIE, cookieToken.toString())
            response.addHeader(HttpHeaders.SET_COOKIE, cookieId.toString())

            siren.response(200)
        }
    }

    @PostMapping(PathTemplate.Users.LOGOUT)
    fun logout(user: AuthenticatedUser, response: HttpServletResponse): ResponseEntity<*> {
        val res = usersService.logout(user.token)
        return handleResponse(res) {
            val siren = SirenMaker.sirenLogout(it)
            val cookieToken = Cookie("token", null)
            cookieToken.path = "/"
            cookieToken.maxAge = 0
            cookieToken.isHttpOnly = true
            cookieToken.secure = false

            val cookieId = Cookie("id", null)
            cookieId.path = "/"
            cookieId.maxAge = 0
            cookieId.isHttpOnly = true
            cookieId.secure = false

            response.addCookie(cookieId)
            response.addCookie(cookieToken)
            siren.response(200)
        }
    }

    @DeleteMapping(PathTemplate.Users.USER_BY_ID)
    fun deleteUser(@PathVariable id: Int) {
        usersService.deleteUser(id)
    }

    @GetMapping(PathTemplate.Users.RANKINGS)
    fun getRankings(): ResponseEntity<*> {
        val res = usersService.getRankings()
        return handleResponse(res) {
            val siren = SirenMaker.sirenRankings(it)
            ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(siren)
        }
    }

    @GetMapping(PathTemplate.Users.USER_RANKING)
    fun getUserRanking(@PathVariable id: Int): ResponseEntity<*> {
        val res = usersService.getUserRanking(id)
        return handleResponse(res) {
            val siren = SirenMaker.sirenRankings(it)
            ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(siren)
        }
    }

    @GetMapping(PathTemplate.Users.CHECK_SESSION)
    fun check(
        request: HttpServletRequest
    ) : Array<out Cookie>? {
        val cookies = request.cookies
        println("check cookies $cookies")
        return cookies
    }

    @GetMapping(PathTemplate.Users.ABOUT)
    fun about(): ResponseEntity<*> {
        val res = success(AboutOutputModel())
        return handleResponse(res) {
            val siren = SirenMaker.sirenAbout(it)
            ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(siren)
        }
    }
}