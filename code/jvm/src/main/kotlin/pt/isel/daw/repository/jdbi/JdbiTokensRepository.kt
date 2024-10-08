package pt.isel.daw.repository.jdbi

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import pt.isel.daw.domainmodel.authentication.Token
import pt.isel.daw.domainmodel.authentication.TokenValidationInfo
import pt.isel.daw.repository.TokensRepository

class JdbiTokensRepository(private val handle: Handle) : TokensRepository {
    override fun createToken(token: Token, maxTokens: Int) {
        handle.createUpdate(
            "delete from tokens " +
                    " where user_id = :user_id and token_validation in (select token_validation from tokens " +
                    "where user_id = :user_id" +
                    " order by last_used_at desc offset :offset)"
        )
            .bind("user_id", token.userId)
            .bind("offset", if (maxTokens > 1) maxTokens - 1 else 1)
            .execute()

        handle.createUpdate(
            "insert into tokens(user_id, token_validation, created_at, last_used_at)" +
                    " values (:user_id, :token_validation, :created_at, :last_used_at)"
        )
            .bind("user_id", token.userId)
            .bind("token_validation", token.tokenValidationInfo.validationInfo)
            .bind("created_at", token.createdAt.epochSeconds)
            .bind("last_used_at", token.lastUsedAt.epochSeconds)
            .execute()
    }

    override fun updateLastUsedToken(token: Token, now: Instant) {
        handle.createUpdate(
            "update tokens set last_used_at = :last_used_at where token_validation = :validation_information"
        )
            .bind("last_used_at", now.epochSeconds)
            .bind("validation_information", token.tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun deleteToken(token: TokenValidationInfo) = handle.createUpdate(
        "delete from tokens where token_validation = :token_validation"
    )
        .bind("token_validation", token.validationInfo)
        .execute() == 0
}