package at.d4m.spring.data.rethinkdb

import at.d4m.spring.data.rethinkdb.mapping.MappingRethinkDbEntityInformation
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbEntityInformation
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbPersistentEntity
import at.d4m.spring.data.rethinkdb.template.RethinkDbOperations
import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.core.support.RepositoryFactorySupport

/**
 * @author Christoph Muck
 */
class RethinkDbRepositoryFactory(
        private val operations: RethinkDbOperations
) : RepositoryFactorySupport() {

    val mappingContext = operations.converter.mappingContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?, ID : Any?> getEntityInformation(domainClass: Class<T>?): RethinkDbEntityInformation<T, ID> {
        val persistentEntity = mappingContext.getRequiredPersistentEntity(domainClass) as RethinkDbPersistentEntity<T>
        return MappingRethinkDbEntityInformation<T, ID>(persistentEntity)
    }

    override fun getTargetRepository(metadata: RepositoryInformation): Any {
        val entityInformation: EntityInformation<*, Any> = getEntityInformation(metadata.domainType)
        return getTargetRepositoryViaReflection(metadata, entityInformation, operations)
    }

    override fun getRepositoryBaseClass(metadata: RepositoryMetadata?): Class<*> {
        return SimpleRethinkDbRepository::class.java
    }
}