package at.d4m.spring.data.rethinkdb

import at.d4m.spring.data.rethinkdb.mapping.RethinkDbEntityInformation
import at.d4m.spring.data.rethinkdb.template.RethinkDbOperations
import org.slf4j.LoggerFactory
import org.springframework.data.repository.CrudRepository
import org.springframework.util.Assert
import java.util.*

/**
 * @author Christoph Muck
 */
class SimpleRethinkDbRepository<T : Any, ID>(
        private val entityInformation: RethinkDbEntityInformation<T, ID>,
        private val rethinkDbOperations: RethinkDbOperations
) : CrudRepository<T, ID> {

    companion object {
        val LOG = LoggerFactory.getLogger(SimpleRethinkDbRepository::class.java)!!
    }

    override fun delete(entity: T) {
        Assert.notNull(entity, "Entity is null!")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAll(): List<T> {
        return rethinkDbOperations.find(entityInformation.javaType, entityInformation.tableName)
    }

    override fun count(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll() {
        rethinkDbOperations.remove(entityInformation.tableName)
    }

    override fun deleteAll(entities: Iterable<T>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <S : T> saveAll(entities: Iterable<S>?): Iterable<S> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(id: ID): Optional<T> {
        val entity = rethinkDbOperations.findById(id, entityInformation.javaType, entityInformation.tableName)
        return Optional.ofNullable(entity)
    }

    override fun <S : T> save(entity: S): S {
        rethinkDbOperations.save(entity, entityInformation.tableName)
        return entity
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