package at.d4m.spring.data.rethinkdb

import at.d4m.spring.data.rethinkdb.mapping.RethinkDbEntityInformation
import at.d4m.spring.data.rethinkdb.template.RethinkDbOperations
import org.slf4j.LoggerFactory
import org.springframework.data.repository.CrudRepository
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
        deleteById(entityInformation.getRequiredId(entity))
    }

    override fun findAll(): List<T> {
        return rethinkDbOperations.find(entityInformation.javaType, entityInformation.tableName)
    }

    override fun count(): Long {
        return findAll().size.toLong()
    }

    override fun deleteAll() {
        rethinkDbOperations.remove(entityInformation.tableName)
    }

    override fun deleteAll(entities: Iterable<T>) {
        entities.forEach { delete(it) }
    }

    override fun <S : T> saveAll(entities: Iterable<S>): Iterable<S> {
        return entities.map { save(it) }
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
        return findById(id).isPresent
    }

    override fun deleteById(id: ID) {
        rethinkDbOperations.remove(entityInformation.tableName, id)
    }

    override fun findAllById(ids: Iterable<ID>): Iterable<T> {
        return ids.mapNotNull { findById(it).orElse(null) }
    }
}