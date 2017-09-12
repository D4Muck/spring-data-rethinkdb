package at.d4m.spring.data.rethinkdb

import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import java.io.Serializable

/**
 * @author Christoph Muck
 */
class RethinkDbRepositoryFactoryBean<T : Repository<S, ID>, S, ID : Serializable>
(repositoryInterface: Class<out T>) : RepositoryFactoryBeanSupport<T, S, ID>(repositoryInterface) {

    private lateinit var operations: RethinkDbOperations

    fun setRethinkDbOperations(operations: RethinkDbOperations) {
        this.operations = operations
    }

    override fun createRepositoryFactory(): RepositoryFactorySupport {
        return RethinkDbRepositoryFactory(operations)
    }

}