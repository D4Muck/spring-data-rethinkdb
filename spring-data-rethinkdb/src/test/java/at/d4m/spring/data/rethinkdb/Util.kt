package at.d4m.spring.data.rethinkdb

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

@Suppress("UNCHECKED_CAST")
fun Any.toMap(): MutableMap<String, Any> {
    val properties = this::class.memberProperties as Collection<KProperty1<Any, Any?>>
    val map = mutableMapOf<String, Any>()
    properties.forEach {
        val value = it.get(this)
        if (value != null) {
            map[it.name] = value.toString()
        }
    }
    return map
}
