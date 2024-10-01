package pt.isel.daw

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import pt.isel.daw.domainmodel.authentication.UsersDomainConfig
import pt.isel.daw.domainmodel.authentication.Sha256TokenEncoder
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Clock
import pt.isel.daw.repository.jdbi.configureWithAppRequirements


@SpringBootApplication
class GomokuApplication {
    @Bean
    fun jdbi(): Jdbi {
        val jdbcDatabaseURL =
            System.getenv("JDBC_DATABASE_URL"/*"JDBC_DOCKER_URL"*/)
                ?: "jdbc:postgresql://localhost/postgres?user=postgres&password=lsg11"
                 //"jdbc:postgresql://localhost/postgres?user=postgres&password=postgres"
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcDatabaseURL)
        return Jdbi.create(dataSource).configureWithAppRequirements()
    }

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()

    @Bean
    fun clock() = Clock.System

    @Bean
    fun usersDomainConfig() = UsersDomainConfig(
        tokenSizeInBytes = 256 / 8,
        tokenTtl = 24.hours,
        tokenRollingTtl = 1.hours,
        maxTokensPerUser = 1
    )
}

fun main(args: Array<String>) {
    runApplication<GomokuApplication>(*args)
}