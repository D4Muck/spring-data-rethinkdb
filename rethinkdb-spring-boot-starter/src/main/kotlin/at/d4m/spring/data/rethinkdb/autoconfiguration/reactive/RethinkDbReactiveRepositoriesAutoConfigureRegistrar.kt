package at.d4m.spring.data.rethinkdb.autoconfiguration.reactive

import at.d4m.spring.data.rethinkdb.reactive.EnableReactiveRethinkDBRepositories
import at.d4m.spring.data.rethinkdb.reactive.ReactiveRethinkDbRepositoryConfigurationExtension
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
import org.springframework.data.repository.config.RepositoryConfigurationExtension

/**
 * @author Christoph Muck
 */
open class RethinkDbReactiveRepositoriesAutoConfigureRegistrar : AbstractRepositoryConfigurationSourceSupport() {

    override fun getConfiguration(): Class<*> = EnableReactiveRethinkDbRepositoriesConfiguration::class.java

    override fun getRepositoryConfigurationExtension(): RepositoryConfigurationExtension = ReactiveRethinkDbRepositoryConfigurationExtension()

    override fun getAnnotation(): Class<out Annotation> = EnableReactiveRethinkDBRepositories::class.java

    @EnableReactiveRethinkDBRepositories
    open class EnableReactiveRethinkDbRepositoriesConfiguration
}