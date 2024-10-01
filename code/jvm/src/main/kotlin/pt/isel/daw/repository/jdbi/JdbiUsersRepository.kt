package pt.isel.daw.repository.jdbi

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import pt.isel.daw.domainmodel.authentication.Token
import pt.isel.daw.domainmodel.authentication.TokenValidationInfo
import pt.isel.daw.domainmodel.user.Ranking
import pt.isel.daw.domainmodel.user.User
import pt.isel.daw.repository.UsersRepository

class JdbiUsersRepository(private val handle: Handle) : UsersRepository {
    override fun getById(id: Int): User? = handle.createQuery(
        "select id, username, email, password from users where id = :id"
    )
        .bind("id", id)
        .mapTo(User::class.java)
        .singleOrNull()

    override fun getByUsername(username: String): User? =
        handle.createQuery("select * from users where username = :username")
            .bind("username", username)
            .mapTo(User::class.java)
            .singleOrNull()

    override fun createUser(name: String, email: String, password: String): Int = handle.createUpdate(
        "insert into users(username, email, password) values (:username, :email, :password)"
    )
        .bind("username", name)
        .bind("email", email)
        .bind("password", password)
        .executeAndReturnGeneratedKeys()
        .mapTo(Int::class.java)
        .one()

    override fun getByToken(token: TokenValidationInfo): Pair<User, Token>? = handle.createQuery(
        "select id, username, email, password, token_validation, created_at, last_used_at from users " +
                "inner join tokens  on users.id = tokens.user_id where token_validation = :validation_information"
    )
        .bind("validation_information", token.validationInfo)
        .mapTo(UserAndTokenModel::class.java)
        .singleOrNull()?.userAndToken

    override fun getRankings(): List<Ranking> = handle.createQuery(
            "SELECT r.user_id, u.username, r.games_played, r.games_won, r.games_drawn," +
                    " case when r.games_played = 0 then 0 else r.games_won * 100.0 / r.games_played end as win_rate" +
                    " from rankings r join users u on r.user_id = u.id  order by games_won desc, games_drawn desc"
    )
        .mapTo(Ranking::class.java)
        .list()

    override fun getUserRanking(userId: Int): Ranking? = handle.createQuery(
        "SELECT user_id, games_played, games_won, games_drawn, " +
                "case when games_played = 0 then 0 else games_won * 1.0 / games_played end as win_rate " +
                "from rankings where user_id = :userId"
    )
        .bind("userId", userId)
        .mapTo(Ranking::class.java)
        .singleOrNull()


    override fun checkUsernameInUse(username: String): Boolean = handle.createQuery(
        "select count(*) from users where username = :username"
    )
        .bind("username", username)
        .mapTo(Int::class.java)
        .single() == 1


    override fun isEmailRegistered(email: String): Boolean = handle.createQuery(
        "select count(*) from users where email = :email"
    )
        .bind("email", email)
        .mapTo(Int::class.java)
        .single() == 1

    override fun delete(id: Int): Boolean = handle.createUpdate(
        "delete from games where player_b = :id or player_w = :id;\n" +
                "delete from rankings where user_id = :id;\n" +
                "delete from users where id = :id;\n"
    )
        .bind("id", id)
        .execute() == 0

    override fun checkUserExist(userId: Int) = handle.createQuery(
        "select count(*) from users where id = :userId"
    )
        .bind("userId", userId)
        .mapTo(Int::class.java)
        .single() == 1

    private data class UserAndTokenModel(
        val id: Int,
        val username: String,
        val email: String,
        val password: String,
        val tokenValidation: TokenValidationInfo,
        val createdAt: Long,
        val lastUsedAt: Long
    ) {
        val userAndToken: Pair<User, Token>
            get() = Pair(
                User(id, username, email, password),
                Token(
                    tokenValidation,
                    id,
                    Instant.fromEpochSeconds(createdAt),
                    Instant.fromEpochSeconds(lastUsedAt)
                )
            )
    }
}