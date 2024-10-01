package pt.isel.daw.domainmodel.user

import pt.isel.daw.http.model.UserOutputModel
import java.util.*

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String
) {
    fun toOutputModel() = UserOutputModel(id, username, email)
}