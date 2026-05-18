package krikar.todo.api.models

import tools.jackson.core.JsonParser
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.annotation.JsonDeserialize

/**
 * Represents the three states of a PATCH field:
 * - [Absent]: field was not included in the request body → keep existing value
 * - [Nulled]: field was explicitly set to null → clear the value
 * - [Present]: field was set to a value → update to that value
 */
@JsonDeserialize(using = PatchDeserializer::class)
sealed class Patch<out T> {
    data object Absent : Patch<Nothing>()
    data object Nulled : Patch<Nothing>()
    data class Present<T>(val value: T) : Patch<T>()
}

class PatchDeserializer(
    private val valueDeserializer: ValueDeserializer<*>? = null,
) : ValueDeserializer<Patch<*>>() {

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): ValueDeserializer<*> {
        val innerType = property?.type?.containedType(0)
        val inner = if (innerType != null) ctxt.findNonContextualValueDeserializer(innerType) else null
        return PatchDeserializer(inner)
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Patch<*> {
        val value = valueDeserializer?.deserialize(p, ctxt) ?: p.readValueAs(Any::class.java)
        return Patch.Present(value)
    }

    override fun getNullValue(ctxt: DeserializationContext): Patch<*> = Patch.Nulled

    override fun getAbsentValue(ctxt: DeserializationContext): Patch<*> = Patch.Absent
}



