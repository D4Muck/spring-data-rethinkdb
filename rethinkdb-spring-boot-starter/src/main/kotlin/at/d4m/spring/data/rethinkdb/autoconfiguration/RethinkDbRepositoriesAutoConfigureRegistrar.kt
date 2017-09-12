package at.d4m.spring.data.rethinkdb.autoconfiguration

import at.d4m.spring.data.rethinkdb.EnableRethinkDBRepositories
import at.d4m.spring.data.rethinkdb.RethinkDbRepositoryConfigurationExtension
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
import org.springframework.data.repository.config.RepositoryConfigurationExtension

/**
 * @author Christoph Muck
 */
open class RethinkDbRepositoriesAutoConfigureRegistrar : AbstractRepositoryConfigurationSourceSupport() {

    override fun getConfiguration(): Class<*> = EnableRethinkDbRepositoriesConfiguration::class.java

    override fun getRepositoryConfigurationExtension(): RepositoryConfigurationExtension = RethinkDbRepositoryConfigurationExtension()

    override fun getAnnotation(): Class<out Annotation> = EnableRethinkDBRepositories::class.java

    @EnableRethinkDBRepositories
    open class EnableRethinkDbRepositoriesConfiguration
}