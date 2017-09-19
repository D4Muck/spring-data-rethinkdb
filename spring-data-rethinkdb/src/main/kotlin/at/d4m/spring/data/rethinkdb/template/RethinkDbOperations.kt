package at.d4m.spring.data.rethinkdb.template

import at.d4m.spring.data.rethinkdb.convert.RethinkDbConverter
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * @author Christoph Muck
 */

interface RethinkDbOperations {
    val converter: RethinkDbConverter
    fun save(obj: Any, table: String): Completable
    fun <T> find(entityClass: Class<T>, table: String): Flowable<T>
    fun <ID, T> findById(id: ID, entityClass: Class<T>, table: String): Maybe<T>
    fun <ID> remove(table: String, id: ID? = null): Completable
    fun remove(table: String): Completable
    fun <T> changeFeed(entityClass: Class<T>, table: String): Flowable<Change<T>>
}

data class Change<out T>(
        val value: T?,
        val event: ChangeEvent
)

enum class ChangeEvent {
    INITIAL, CREATED, DELETED
}