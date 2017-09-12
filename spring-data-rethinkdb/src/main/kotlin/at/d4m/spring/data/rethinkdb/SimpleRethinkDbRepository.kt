package at.d4m.spring.data.rethinkdb

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.core.EntityInformation
import org.springframework.util.Assert
import java.util.*

/**
 * @author Christoph Muck
 */
class SimpleRethinkDbRepository<T, ID>(
        private val entityInformation: EntityInformation<T, ID>,
        private val rethinkDbOperations: RethinkDbOperations
) : CrudRepository<T, ID> {

    override fun delete(entity: T) {
        Assert.notNull(entity, "Entity is null!")

        entityInformation.isNew(entity)

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAll(): Iterable<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun count(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll(entities: Iterable<T>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <S : T> saveAll(entities: Iterable<S>?): Iterable<S> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(id: ID): Optional<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <S : T> save(entity: S): S {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun existsById(id: ID): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteById(id: ID) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAllById(ids: Iterable<ID>?): Iterable<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}