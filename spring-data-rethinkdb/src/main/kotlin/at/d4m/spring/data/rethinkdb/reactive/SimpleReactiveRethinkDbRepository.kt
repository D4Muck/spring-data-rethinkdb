package at.d4m.spring.data.rethinkdb.reactive

import at.d4m.spring.data.rethinkdb.mapping.RethinkDbEntityInformation
import at.d4m.spring.data.rethinkdb.template.RethinkDbOperations
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.springframework.data.repository.reactive.RxJava2CrudRepository

/**
 * @author Christoph Muck
 */
class SimpleReactiveRethinkDbRepository<T : Any, ID>(
        private val entityInformation: RethinkDbEntityInformation<T, ID>,
        private val rethinkDbOperations: RethinkDbOperations
) : RxJava2CrudRepository<T, ID> {

    override fun findAllById(ids: MutableIterable<ID>?): Flowable<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAllById(idStream: Flowable<ID>?): Flowable<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(id: ID): Maybe<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(id: Single<ID>?): Maybe<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun existsById(id: Single<ID>?): Single<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun existsById(id: ID): Single<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(entity: T): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAll(): Flowable<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteById(id: ID): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll(entityStream: Flowable<out T>?): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll(): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll(entities: MutableIterable<T>?): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <S : T> save(entity: S): Single<S> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun count(): Single<Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <S : T> saveAll(entities: MutableIterable<S>?): Flowable<S> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <S : T> saveAll(entityStream: Flowable<S>?): Flowable<S> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}