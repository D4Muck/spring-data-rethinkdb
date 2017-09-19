package at.d4m.spring.data.rethinkdb.reactive

import at.d4m.spring.data.rethinkdb.template.Change
import io.reactivex.Flowable
import org.springframework.data.repository.reactive.RxJava2CrudRepository

/**
 * @author Christoph Muck
 */
interface ReactiveRethinkDbRepository<T, ID> : RxJava2CrudRepository<T, ID> {
    fun changeFeed(): Flowable<Change<T>>
}
