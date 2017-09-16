package at.d4m.spring.data.rethinkdb.reactive

import at.d4m.spring.data.rethinkdb.AbstractRethinkDbRepositoryConfigurationExtension

/**
 * @author Christoph Muck
 */
class ReactiveRethinkDbRepositoryConfigurationExtension : AbstractRethinkDbRepositoryConfigurationExtension() {

    override fun getRepositoryFactoryBeanClassName(): String {
        return ReactiveRethinkDbRepositoryFactoryBean::class.java.name
    }
}