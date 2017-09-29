package at.d4m.spring.data.rethinkdb.reactive

import at.d4m.spring.data.rethinkdb.template.RethinkDbOperations
import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import java.io.Serializable

/**
 * @author Christoph Muck
 */
class ReactiveRethinkDbRepositoryFactoryBean<T : Repository<S, ID>, S, ID : Serializable>(repositoryInterface: Class<out T>)
    : RepositoryFactoryBeanSupport<T, S, ID>(repositoryInterface) {

    private lateinit var operations: RethinkDbOperations

    override fun createRepositoryFactory(): RepositoryFactorySupport {
        return ReactiveRethinkDbRepositoryFactory(operations)
    }

    @Suppress("UNUSED")
    fun setRethinkDbOperations(operations: RethinkDbOperations) {
        this.operations = operations
        super.setMappingContext(operations.converter.mappingContext)
    }
}