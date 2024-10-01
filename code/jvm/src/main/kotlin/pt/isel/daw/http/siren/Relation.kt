package pt.isel.daw.http.siren

object Relation {
    val self = LinkRelation("self")

    fun getLink(uri: String) = LinkRelation("\"http://localhost:8080\"$uri")

}