package at.d4m.spring.data.rethinkdb.mapping

import org.springframework.data.mapping.PersistentEntity

/**
 * @author Christoph Muck
 */
interface RethinkDbPersistentEntity<T> : PersistentEntity<T, RethinkDbPersistentProperty> {
    val tableName: String
}