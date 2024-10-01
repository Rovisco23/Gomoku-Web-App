package pt.isel.daw.http

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.domainmodel.game.OpeningRule
import pt.isel.daw.http.model.GamePlayInputModel
import pt.isel.daw.http.model.GameStartInputModel
import pt.isel.daw.domainmodel.authentication.AuthenticatedUser
import pt.isel.daw.http.siren.SirenMaker
import pt.isel.daw.service.GamesService
import java.util.*

@RestController
class GamesController(private val gamesService: GamesService) {

    @GetMapping(PathTemplate.Games.GAME_BY_ID)
    fun getGame(@PathVariable id: UUID, user: AuthenticatedUser): ResponseEntity<*> {
        val res = gamesService.getGame(id, user.user.id)
        return handleResponse(res) {
             val siren = SirenMaker.sirenGetGame(it)
            ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(siren)
        }
    }

    @PostMapping(PathTemplate.Games.PLAY)
    fun play(@PathVariable id: UUID, @RequestBody p: GamePlayInputModel, user: AuthenticatedUser): ResponseEntity<*> {
        val res = gamesService.play(id, user.user.id, p.userId, p.row, p.col)
        return handleResponse(res) {
            val siren = SirenMaker.sirenPlay(it.toExternal())
            ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(siren)
        }
    }

    @PostMapping(PathTemplate.Games.START)
    fun startGame(@RequestBody s: GameStartInputModel, user: AuthenticatedUser): ResponseEntity<*> {
        val res = gamesService.start(user.user.id, s.userId, OpeningRule.fromString(s.rule), s.boardSize)
        return handleResponse(res) {
            val siren = SirenMaker.sirenStartGame(it)
            ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(siren)
        }
    }

    @GetMapping(PathTemplate.Games.LOBBY_UPDATE)
    fun checkLobbyUpdate(@PathVariable userId: Int, user: AuthenticatedUser): ResponseEntity<*> {
        val res = gamesService.checkLobbyUpdate(userId, user.user.id)
        return handleResponse(res) {
            val siren = SirenMaker.sirenCheckLobbyUpdate(it)
            ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(siren)
        }
    }

    @PostMapping(PathTemplate.Games.DRAW)
    fun giveUpGame(@PathVariable id: UUID, user: AuthenticatedUser): ResponseEntity<*> {
        val res = gamesService.giveUpGame(id, user.user.id)
        return handleResponse(res) {
            val siren = SirenMaker.sirenGiveUpGame(it.toExternal())
            ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(siren)
        }
    }

    @DeleteMapping(PathTemplate.Games.DELETE)
    fun deleteGame(@PathVariable id: UUID) {
        gamesService.deleteGame(id)
    }
}