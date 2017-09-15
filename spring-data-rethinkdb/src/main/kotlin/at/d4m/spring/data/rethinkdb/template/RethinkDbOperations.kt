package at.d4m.spring.data.rethinkdb.template

import at.d4m.spring.data.rethinkdb.convert.RethinkDbConverter

/**
 * @author Christoph Muck
 */

interface RethinkDbOperations {
    val converter: RethinkDbConverter
    fun save(obj: Any, table: String)
    fun <T> find(entityClass: Class<T>, table: String): List<T>
    fun remove(table: String)
    fun <ID, T> findById(id: ID, entityClass: Class<T>, table: String): T?
}