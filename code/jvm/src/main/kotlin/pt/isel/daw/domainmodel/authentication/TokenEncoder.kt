package pt.isel.daw.domainmodel.authentication

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}