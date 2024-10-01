package pt.isel.daw

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.domainmodel.game.Board
import pt.isel.daw.domainmodel.game.Game
import pt.isel.daw.domainmodel.game.GameState
import pt.isel.daw.domainmodel.game.OpeningRule
import pt.isel.daw.http.model.BoardExternalValues
import pt.isel.daw.http.model.GameExternalValues
import pt.isel.daw.http.model.toCells
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTests {
    private var userId1 = 0
    private var userId2 = 0
    private var token1 = ""
    private var token2 = ""

    private val afterPlayBoard = "B----------------------------------------------------------------------------------" +
            "--------------------------------------------------------------------------------------------------------" +
            "--------------------------------------"

    private val createUserBody1 = """
            {
                "name": "Joaquimtest",
                "email": "joaquimtest@gmail.com",
                "password": "Joaquimtest1+"
            }
        """
    private val loginBody1 = """
            {
                "username": "Joaquimtest",
                "password": "Joaquimtest1+"
            }
        """
    private val createUserBody2 = """
            {
                "name": "Fernandotest",
                "email": "fernandotest@gmail.com",
                "password": "Fernandotest1+"
            }
        """
    private val loginBody2 = """
            {
                "username": "Fernandotest",
                "password": "Fernandotest1+"
            }
        """

    @LocalServerPort
    var port: Int = 0

    @BeforeEach
    fun setup() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        client.post().uri("/api/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createUserBody1)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { userId1 = it["id"].toString().toInt() }

        client.post().uri("/api/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createUserBody2)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { userId2 = it["id"].toString().toInt() }
    }

    @AfterEach
    fun cleanup() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        client.delete().uri("/api/users/$userId1").exchange()
        client.delete().uri("/api/users/$userId2").exchange()
    }


    @Test
    fun startGameFreeStyleTest() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        var gameId1 = ""
        var gameId2 = ""
        lateinit var game: Game

        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody1)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token1 = it["token"].toString() }


        client.post().uri("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .bodyValue(
                """{
                "userId": $userId1,
                "rule": "Free Style",
                "boardSize": 15
            }"""
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<String> { gameId2 = it.toString() }

        assertEquals("Lobby Created", gameId2)

        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody2)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token2 = it["token"].toString() }

        client.post().uri("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token2")
            .bodyValue(
                """{
                "userId": $userId2,
                "rule": "Free Style",
                "boardSize": 15
            }"""
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<String> { gameId1 = it }

        client.get().uri("/api/lobbies/$userId1")
            .header("Authorization", "Bearer $token1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<String> { gameId2 = it }

        assert(gameId1 == gameId2)

        client.get().uri("/api/games/$gameId1")
            .header("Authorization", "Bearer $token1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> {
                val board = it["board"] as Map<*, *>
                game = Game(
                    UUID.fromString(it["id"].toString()),
                    Board(
                        board["cells"].toString().toCells(15),
                        board["size"].toString().toInt(),
                        board["turn"].toString()[0]
                    ),
                    GameState.fromString(it["state"].toString()),
                    OpeningRule.fromString(it["rule"].toString()),
                    it["playerB"].toString().toInt(),
                    it["playerW"].toString().toInt()
                )
            }

        assertEquals(15, game.board.getSize())
        assertEquals(OpeningRule.FREE_STYLE, game.openingRule)
        assertEquals(userId1, game.playerB)
        assertEquals(userId2, game.playerW)

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .exchange()

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token2")
            .exchange()

        client.delete().uri("/api/games/$gameId1").exchange()
    }

    @Test
    fun startGameProTest() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        var gameId1 = ""
        var gameId2 = ""

        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody1)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token1 = it["token"].toString() }


        client.post().uri("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .bodyValue(
                """{
                "userId": $userId1,
                "rule": "Pro",
                "boardSize": 15
            }"""
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<String> { gameId2 = it.toString() }

        assertEquals("Lobby Created", gameId2)

        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody2)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token2 = it["token"].toString() }

        client.post().uri("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token2")
            .bodyValue(
                """{
                "userId": $userId2,
                "rule": "Pro",
                "boardSize": 15
            }"""
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<String> { gameId1 = it }

        client.get().uri("/api/lobbies/$userId1")
            .header("Authorization", "Bearer $token1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<String> { gameId2 = it }

        assert(gameId1 == gameId2)

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .exchange()

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token2")
            .exchange()

        client.delete().uri("/api/games/$gameId1").exchange()
    }

    @Test
    fun getGameTest() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        var gameId1 = ""
        lateinit var game: Game

        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody1)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token1 = it["token"].toString() }


        client.post().uri("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .bodyValue(
                """{
                "userId": $userId1,
                "rule": "Free Style",
                "boardSize": 15
            }"""
            )
            .exchange()
            .expectStatus().isCreated

        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody2)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token2 = it["token"].toString() }

        client.post().uri("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token2")
            .bodyValue(
                """{
                "userId": $userId2,
                "rule": "Free Style",
                "boardSize": 15
            }"""
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<String> { gameId1 = it }

        client.get().uri("/api/lobbies/$userId1")
            .header("Authorization", "Bearer $token1")
            .exchange()
            .expectStatus().isOk

        client.get().uri("/api/games/$gameId1")
            .header("Authorization", "Bearer $token1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> {
                val board = it["board"] as Map<*, *>
                game = Game(
                    UUID.fromString(it["id"].toString()),
                    Board(
                        board["cells"].toString().toCells(15),
                        board["size"].toString().toInt(),
                        board["turn"].toString()[0]
                    ),
                    GameState.fromString(it["state"].toString()),
                    OpeningRule.fromString(it["rule"].toString()),
                    it["playerB"].toString().toInt(),
                    it["playerW"].toString().toInt()
                )
            }

        assertEquals(15, game.board.getSize())
        assertEquals(OpeningRule.FREE_STYLE, game.openingRule)
        assertEquals(userId1, game.playerB)
        assertEquals(userId2, game.playerW)

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .exchange()

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token2")
            .exchange()

        client.delete().uri("/api/games/$gameId1").exchange()
    }

    @Test
    fun playGameTest() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        var gameId1 = ""
        lateinit var game: GameExternalValues

        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody1)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token1 = it["token"].toString() }


        client.post().uri("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .bodyValue(
                """{
                "userId": $userId1,
                "rule": "Free Style",
                "boardSize": 15
            }"""
            )
            .exchange()
            .expectStatus().isCreated

        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody2)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token2 = it["token"].toString() }

        client.post().uri("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token2")
            .bodyValue(
                """{
                "userId": $userId2,
                "rule": "Free Style",
                "boardSize": 15
            }"""
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<String> { gameId1 = it }

        client.get().uri("/api/lobbies/$userId1")
            .header("Authorization", "Bearer $token1")
            .exchange()
            .expectStatus().isOk

        client.post().uri("/api/games/$gameId1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .bodyValue(
                """{
                "userId": $userId1,
                "row": 1,
                "col": "a"
            }"""
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> {
                val board = it["board"] as Map<*, *>
                game = GameExternalValues(
                    it["id"].toString(),
                    BoardExternalValues(
                        board["cells"].toString(),
                        board["size"].toString().toInt(),
                        board["turn"].toString()[0]
                    ),
                    it["state"].toString(),
                    it["playerW"].toString().toInt(),
                    it["playerB"].toString().toInt(),
                    it["winner"].toString().toIntOrNull()
                )
            }

        assertEquals(15, game.board.size)
        assertEquals('W', game.board.turn)
        assertEquals("On Going", game.state)
        assertEquals(afterPlayBoard, game.board.cells)
        assertEquals(userId1, game.playerB)
        assertEquals(userId2, game.playerW)

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .exchange()

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token2")
            .exchange()

        client.delete().uri("/api/games/$gameId1").exchange()
    }

    @Test
    fun giveUpGameTest() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        var gameId1 = ""
        lateinit var game: GameExternalValues

        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody1)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token1 = it["token"].toString() }

        client.post().uri("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .bodyValue(
                """{
                "userId": $userId1,
                "rule": "Free Style",
                "boardSize": 15
            }"""
            )
            .exchange()
            .expectStatus().isCreated

        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody2)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token2 = it["token"].toString() }

        client.post().uri("/api/games")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token2")
            .bodyValue(
                """{
                "userId": $userId2,
                "rule": "Free Style",
                "boardSize": 15
            }"""
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<String> { gameId1 = it }

        client.get().uri("/api/lobbies/$userId1")
            .header("Authorization", "Bearer $token1")
            .exchange()
            .expectStatus().isOk

        client.post().uri("/api/giveup/$gameId1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> {
                val board = it["board"] as Map<*, *>
                game = GameExternalValues(
                    it["id"].toString(),
                    BoardExternalValues(
                        board["cells"].toString(),
                        board["size"].toString().toInt(),
                        board["turn"].toString()[0]
                    ),
                    it["state"].toString(),
                    it["playerW"].toString().toInt(),
                    it["playerB"].toString().toInt(),
                    it["winner"].toString().toIntOrNull()
                )
            }

        assertEquals(userId2, game.winner)

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token1")
            .exchange()

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token2")
            .exchange()

        client.delete().uri("/api/games/$gameId1").exchange()
    }

}