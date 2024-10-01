package pt.isel.daw.repository

import kotlinx.datetime.Instant
import pt.isel.daw.domainmodel.authentication.Token
import pt.isel.daw.domainmodel.authentication.TokenValidationInfo

interface TokensRepository {
    fun createToken(token: Token, maxTokens: Int)
    fun updateLastUsedToken(token: Token, now: Instant)
    fun deleteToken(token: TokenValidationInfo) : Boolean
}