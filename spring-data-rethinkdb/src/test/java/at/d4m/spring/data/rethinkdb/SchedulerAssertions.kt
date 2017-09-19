package at.d4m.spring.data.rethinkdb

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.jupiter.api.Assertions

/**
 * @author Christoph Muck
 */
fun Completable.assertIoScheduler(): Completable {
    return this.doOnComplete { assertCurrentThreadOnIoScheduler() }
}

fun <T> Maybe<T>.assertIoScheduler(): Maybe<T> {
    return this.doOnComplete { assertCurrentThreadOnIoScheduler() }
}

fun <T> Single<T>.assertIoScheduler(): Single<T> {
    return this.doOnSuccess { assertCurrentThreadOnIoScheduler() }
}

fun <T> Flowable<T>.assertIoScheduler(): Flowable<T> {
    return this.doOnComplete { assertCurrentThreadOnIoScheduler() }
}

fun assertCurrentThreadOnIoScheduler() {
    val threadName = Thread.currentThread().name
    val prefix = "RxCachedThreadScheduler"
    Assertions.assertTrue(
            threadName.startsWith(prefix),
            "Expected Thread name to start with '$prefix', but name was '$threadName'"
    )
}