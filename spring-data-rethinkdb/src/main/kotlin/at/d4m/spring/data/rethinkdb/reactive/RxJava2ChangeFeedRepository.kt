package at.d4m.spring.data.rethinkdb.reactive

import at.d4m.spring.data.rethinkdb.template.RethinkDbChange
import io.reactivex.Flowable
import org.springframework.data.repository.reactive.RxJava2CrudRepository

/**
 * @author Christoph Muck
 */
interface RxJava2ChangeFeedRepository<T, ID> : RxJava2CrudRepository<T, ID> {
    fun changeFeed(): Flowable<Change<T>>
}

data class Change<out T>(
        val value: T?,
        val event: ChangeEvent
)

enum class ChangeEvent {
    INITIAL, CREATED, DELETED
}
