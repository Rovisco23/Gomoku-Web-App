package pt.isel.daw.repository

import pt.isel.daw.domainmodel.authentication.Token
import pt.isel.daw.domainmodel.authentication.TokenValidationInfo
import pt.isel.daw.domainmodel.user.Ranking
import pt.isel.daw.domainmodel.user.User

interface UsersRepository {
    fun getById(id: Int): User?
    fun createUser(name: String, email: String, password: String): Int
    fun getRankings(): List<Ranking>
    fun getUserRanking(userId: Int): Ranking?
    fun isEmailRegistered(email: String): Boolean
    fun checkUsernameInUse(username: String): Boolean
    fun delete(id: Int): Boolean
    fun checkUserExist(userId: Int): Boolean
    fun getByUsername(username: String): User?
    fun getByToken(token: TokenValidationInfo): Pair<User, Token>?
}