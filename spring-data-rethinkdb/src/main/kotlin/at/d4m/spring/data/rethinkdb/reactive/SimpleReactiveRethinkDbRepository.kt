package at.d4m.spring.data.rethinkdb.reactive

import at.d4m.spring.data.rethinkdb.mapping.RethinkDbEntityInformation
import at.d4m.spring.data.rethinkdb.template.RethinkDbChange
import at.d4m.spring.data.rethinkdb.template.RethinkDbChangeEvent
import at.d4m.spring.data.rethinkdb.template.RethinkDbOperations
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * @author Christoph Muck
 */
class SimpleReactiveRethinkDbRepository<T : Any, ID>(
        private val entityInformation: RethinkDbEntityInformation<T, ID>,
        private val operations: RethinkDbOperations
) : RxJava2ChangeFeedRepository<T, ID> {

    override fun findAllById(ids: Iterable<ID>): Flowable<T> {
        return Flowable.merge(ids.map { findById(it).toFlowable() })
    }

    override fun findAllById(idStream: Flowable<ID>): Flowable<T> {
        return idStream.flatMap { findById(it).toFlowable() }
    }

    override fun findById(id: ID): Maybe<T> {
        return operations.findById(id, entityInformation.javaType, entityInformation.tableName)
    }

    override fun findById(id: Single<ID>): Maybe<T> {
        return id.flatMapMaybe { findById(it) }
    }

    override fun existsById(id: Single<ID>): Single<Boolean> {
        return id.flatMap { existsById(it) }
    }

    override fun existsById(id: ID): Single<Boolean> {
        return findById(id).isEmpty.map { !it }
    }

    override fun delete(entity: T): Completable {
        return deleteById(entityInformation.getRequiredId(entity))
    }

    override fun findAll(): Flowable<T> {
        return operations.find(entityInformation.javaType, entityInformation.tableName)
    }

    override fun deleteById(id: ID): Completable {
        return operations.remove(entityInformation.tableName, id)

    }

    override fun deleteAll(entityStream: Flowable<out T>): Completable {
        return entityStream.flatMapCompletable { delete(it) }
    }

    override fun deleteAll(): Completable {
        return operations.remove(entityInformation.tableName)
    }

    override fun deleteAll(entities: Iterable<T>): Completable {
        return Completable.merge(entities.map { delete(it) })
    }

    override fun <S : T> save(entity: S): Single<S> {
        return operations.save(entity, entityInformation.tableName)
                .toSingle { entity }
    }

    override fun count(): Single<Long> {
        return findAll().count()
    }

    override fun <S : T> saveAll(entities: Iterable<S>): Flowable<S> {
        return Flowable.merge(entities.map { save(it).toFlowable() })
    }

    override fun <S : T> saveAll(entityStream: Flowable<S>): Flowable<S> {
        return entityStream.flatMapSingle { save(it) }
    }

    override fun changeFeed(): Flowable<Change<T>> {
        return operations.changeFeed(entityInformation.javaType, entityInformation.tableName)
                .map { it.asChange() }
    }

    private fun <T> RethinkDbChange<T>.asChange(): Change<T> {
        return Change(this.value, this.event.asChangeEvent())
    }

    private fun RethinkDbChangeEvent.asChangeEvent(): ChangeEvent {
        return when (this) {
            RethinkDbChangeEvent.INITIAL -> ChangeEvent.INITIAL
            RethinkDbChangeEvent.CREATED -> ChangeEvent.CREATED
            RethinkDbChangeEvent.DELETED -> ChangeEvent.DELETED
        }
    }
}