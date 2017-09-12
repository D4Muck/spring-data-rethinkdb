package at.d4m.spring.data.rethinkdb

import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.core.support.ReflectionEntityInformation
import org.springframework.data.repository.core.support.RepositoryFactorySupport

/**
 * @author Christoph Muck
 */
class RethinkDbRepositoryFactory(
        val operations: RethinkDbOperations
) : RepositoryFactorySupport() {

    override fun <T : Any?, ID : Any?> getEntityInformation(domainClass: Class<T>?): EntityInformation<T, ID> {
        val entityInformation = ReflectionEntityInformation<T, ID>(domainClass)
        return entityInformation
    }

    override fun getTargetRepository(metadata: RepositoryInformation): Any {
        val entityInformation: EntityInformation<*, Any> = getEntityInformation(metadata.domainType)
        return getTargetRepositoryViaReflection(metadata, entityInformation, operations)
    }

    override fun getRepositoryBaseClass(metadata: RepositoryMetadata?): Class<*> {
        return SimpleRethinkDbRepository::class.java
    }
}