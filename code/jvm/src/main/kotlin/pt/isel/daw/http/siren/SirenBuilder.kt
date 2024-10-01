package pt.isel.daw.http.siren

import java.net.URI
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import pt.isel.daw.http.siren.SirenModel.Companion.MEDIA_TYPE

class SirenBuilder<T>(
    private val properties: T
) {
    private val links = mutableListOf<LinkModel>()
    private val klasses = mutableListOf<String>()
    private val entities = mutableListOf<EntityModel<*>>()
    private val actions = mutableListOf<ActionModel>()

    fun klass(value : String)  {
        klasses.add(value)
    }

    fun link(href: String, rel: LinkRelation) {
        links.add(LinkModel(listOf(rel.value), URI(href).toASCIIString()))
    }

    fun action(name: String, href: URI, method: HttpMethod) {
        val builder = ActionBuilder(name, href, method)
        actions.add(builder.build())
    }

    fun <T> entity(value: T, rel: String) {
        val builder = EntityBuilder(value, listOf(rel))
        entities.add(builder.build())
    }

    fun build(): SirenModel<T> = SirenModel(
        klass = klasses,
        properties = properties,
        links = links,
        actions = actions,
        entities = entities
    )
}

class ActionBuilder(
    private val name: String,
    private val href: URI,
    private val method: HttpMethod
) {

    private val fields = mutableListOf<FieldModel>()

    fun textField(name: String, value: String? = null) =
        fields.add(FieldModel(name, "text", value))

    fun numberField(name: String, value: String? = null) =
        fields.add(FieldModel(name, "number", value))

    fun hiddenField(name: String, value: String) =
        fields.add(FieldModel(name, "hidden", value))

    fun build(): ActionModel {
        val fields = mutableListOf<FieldModel>()

        return ActionModel(
            name,
            href.toASCIIString(),
            method.name(),
            fields
        )
    }
}

class EntityBuilder<T>(
    private val properties: T,
    private val rel: List<String>
) {
    private val links = mutableListOf<LinkModel>()

    fun link(href: URI, rel: String) =
        links.add(LinkModel(listOf(rel), href.toASCIIString()))

    fun build(): EntityModel<T> = EntityModel(
        properties = properties,
        links = links,
        rel = rel
    )
}

fun <T> siren(properties: T, init: SirenBuilder<T>.() -> Unit): SirenModel<T> {
    val builder = SirenBuilder(properties)
    builder.init()
    return builder.build()
}

fun <T> SirenModel<T>.response(status: Int) = ResponseEntity
    .status(status)
    .header("Content-Type", MEDIA_TYPE)
    .body(this)

fun <T> SirenModel<T>.responseRedirect(status: Int, headers: String) = ResponseEntity
    .status(status)
    .header("Location", headers)
    .body(this)