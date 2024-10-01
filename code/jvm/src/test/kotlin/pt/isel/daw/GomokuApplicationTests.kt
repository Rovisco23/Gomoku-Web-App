package pt.isel.daw

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.core.StringContains.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@SpringBootTest
class GomokuApplicationTests {

	@Autowired
	private lateinit var controller: GomokuApplication

	@Test
	fun contextLoads() {
		assertThat(controller).isNotNull()
	}
	/*
    @Value("\${local.server.port}")
    private val port: Int = 0


    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun greetingShouldReturnDefaultMessage() {
        assertThat(this.restTemplate.getForObject("http://localhost:$port/",
                String::class.java)).contains("Hello, World")
    }


    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun shouldReturnDefaultMessage() {
        mockMvc.perform(get("/"))
                .andDo { System.out.println() }
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Hello, World")))
    } */
}