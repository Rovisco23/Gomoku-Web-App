package pt.isel.daw.domainmodel.authentication

import pt.isel.daw.domainmodel.user.User

data class AuthenticatedUser(
    val user: User,
    val token: String
)