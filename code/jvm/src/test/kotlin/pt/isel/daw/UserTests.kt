package pt.isel.daw

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.domainmodel.user.User
import pt.isel.daw.http.model.UserOutputModel

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTests {
    private val name = "Joaquimtest"
    private val email = "joaquimtest@gmail.com"
    private val password = "Joaquimtest1+"
    private val body = """
        {
            "name": "$name",
            "email": "$email",
            "password": "$password"
        }
    """
    private val loginBody = """
        {
            "username": "$name",
            "password": "$password"
        }
    """

    @LocalServerPort
    var port: Int = 0

    @Test
    fun createUserTest() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        lateinit var user: User
        client.post().uri("api/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> {
                user = User (
                    it["id"].toString().toInt(),
                    it["username"].toString(),
                    it["email"].toString(),
                    it["password"].toString()
                )
            }
        assertEquals(name, user.username)
        assertEquals(email, user.email)
        assertEquals(password, user.password)
        client.delete().uri("api/users/${user.id}").exchange()
    }

    @Test
    fun getUserTest() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        var userId = 0
        lateinit var user: UserOutputModel
        client.post().uri("api/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> {
                userId = it["id"].toString().toInt()
            }

        client.get().uri("api/users/$userId")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> {
                user = UserOutputModel (
                    it["id"].toString().toInt(),
                    it["username"].toString(),
                    it["email"].toString()
                )
            }
        assertEquals(userId, user.id)
        assertEquals(name, user.username)
        assertEquals(email, user.email)
        client.delete().uri("api/users/${user.id}").exchange()
    }

    @Test
    fun loginLogoutTest() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        var userId = 0
        lateinit var token: String
        client.post().uri("api/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> {
                userId = it["id"].toString().toInt()
            }
        client.post().uri("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginBody)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("properties").value<Map<String, Any>> { token = it["token"].toString() }

        client.post().uri("/api/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk

        client.delete().uri("api/users/$userId").exchange()

    }
}