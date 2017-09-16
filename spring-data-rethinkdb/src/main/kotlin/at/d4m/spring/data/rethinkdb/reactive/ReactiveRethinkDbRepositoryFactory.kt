package at.d4m.spring.data.rethinkdb.reactive

import at.d4m.spring.data.rethinkdb.mapping.MappingRethinkDbEntityInformation
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbEntityInformation
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbPersistentEntity
import at.d4m.spring.data.rethinkdb.template.RethinkDbOperations
import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport

/**
 * @author Christoph Muck
 */
class ReactiveRethinkDbRepositoryFactory(
        private val operations: RethinkDbOperations
) : ReactiveRepositoryFactorySupport() {

    private val mappingContext = operations.converter.mappingContext

    override fun <T : Any, ID : Any> getEntityInformation(domainClass: Class<T>): RethinkDbEntityInformation<T, ID> {
        @Suppress("UNCHECKED_CAST")
        val entity = mappingContext.getRequiredPersistentEntity(domainClass) as RethinkDbPersistentEntity<T>
        return MappingRethinkDbEntityInformation(entity)
    }

    override fun getTargetRepository(metadata: RepositoryInformation): Any {
        val entityInformation: EntityInformation<*, Any> = getEntityInformation(metadata.domainType)
        return getTargetRepositoryViaReflection(metadata, entityInformation, operations)
    }

    override fun getRepositoryBaseClass(metadata: RepositoryMetadata?): Class<*> {
        return SimpleReactiveRethinkDbRepository::class.java
    }
}