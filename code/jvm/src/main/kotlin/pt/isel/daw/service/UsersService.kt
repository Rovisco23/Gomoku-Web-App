package pt.isel.daw.service

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.daw.domainmodel.authentication.Token
import pt.isel.daw.domainmodel.authentication.TokenExternalInfo
import pt.isel.daw.domainmodel.authentication.UsersDomain
import pt.isel.daw.domainmodel.user.User
import pt.isel.daw.http.model.Errors
import pt.isel.daw.http.model.UserOutputModel
import pt.isel.daw.http.utils.Result
import pt.isel.daw.http.utils.failure
import pt.isel.daw.http.utils.success
import pt.isel.daw.repository.TransactionManager

typealias UserCreationResult = Result<Errors, User>
typealias UserLoginResult = Result<Errors, TokenExternalInfo>
typealias UserDeleteResult = Result<Errors, Int>
typealias GetUserResult = Result<Errors, UserOutputModel>

@Component
class UsersService(
    private val transactionManager: TransactionManager,
    private val usersDomain: UsersDomain,
    private val clock: Clock
) {
    fun getUser(id: Int): GetUserResult = transactionManager.run {
        val user = it.usersRepository.getById(id)
        if (user == null) {
            failure(Errors.userNotFound)
        } else {
            success(user.toOutputModel())
        }
    }

    fun getUserByToken(token: String): User? {
        if (!usersDomain.canBeToken(token)) {
            return null
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val tokensRepository = it.tokensRepository
            val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
            val userAndToken = usersRepository.getByToken(tokenValidationInfo)
            if (userAndToken != null && usersDomain.isTokenTimeValid(clock, userAndToken.second)) {
                tokensRepository.updateLastUsedToken(userAndToken.second, clock.now())
                userAndToken.first
            } else {
                null
            }
        }
    }

    fun create(name: String, email: String, password: String): UserCreationResult = transactionManager.run {
        val userRep = it.usersRepository
        if (userRep.checkUsernameInUse(name)) {
            failure(Errors.usernameAlreadyInUse)
        } else if (userRep.isEmailRegistered(email)) {
            failure(Errors.emailAlreadyInUse)
        } else if (!usersDomain.isSafePassword(password)) {
            failure(Errors.invalidPassword)
        } else {
            val id = userRep.createUser(name, email, password)
            success(User(id, name, email, password))
        }
    }

    fun login(username: String, password: String): UserLoginResult {
        if (username.isBlank() || password.isBlank()) {
            failure(Errors.userOrPasswordAreInvalid)
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val tokensRepository = it.tokensRepository
            val user = usersRepository.getByUsername(username)
            if (user == null || user.password != password) {
                failure(Errors.userOrPasswordAreInvalid)
            } else {
                val tokenValue = usersDomain.generateTokenValue()
                val now = clock.now()
                val newToken = Token(
                    tokenValidationInfo = usersDomain.createTokenValidationInformation(tokenValue),
                    userId = user.id,
                    createdAt = now,
                    lastUsedAt = now
                )
                tokensRepository.createToken(newToken, usersDomain.maxNumberOfTokensPerUser)
                success(
                    TokenExternalInfo(
                        tokenValue = tokenValue,
                        userId = user.id,
                        username = user.username,
                        tokenExpiration = usersDomain.getTokenExpiration(newToken)
                    )
                )
            }
        }
    }

    fun logout(token: String): Result<Nothing, Boolean> {
        val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
        return transactionManager.run {
            val out = it.tokensRepository.deleteToken(tokenValidationInfo)
            success(out)
        }
    }

    fun getRankings() = transactionManager.run { success(it.usersRepository.getRankings()) }

    fun getUserRanking(id: Int) = transactionManager.run {
        val ranking = it.usersRepository.getUserRanking(id)
        if (ranking == null) {
            failure(Errors.userNotFound)
        } else {
            success(ranking)
        }
    }

    fun deleteUser(id: Int): UserDeleteResult = transactionManager.run {
        val user = it.usersRepository.getById(id)
        if (user == null) {
            failure(Errors.userNotFound)
        } else {
            it.usersRepository.delete(id)
            success(id)
        }
    }
}