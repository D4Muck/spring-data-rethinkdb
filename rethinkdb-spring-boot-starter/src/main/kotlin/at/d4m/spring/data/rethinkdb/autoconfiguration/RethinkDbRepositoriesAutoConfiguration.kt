package at.d4m.spring.data.rethinkdb.autoconfiguration

import at.d4m.spring.data.rethinkdb.RethinkDbRepositoryConfigurationExtension
import at.d4m.spring.data.rethinkdb.RethinkDbRepositoryFactoryBean
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author Christoph Muck
 */
@Configuration
@ConditionalOnMissingBean(
        RethinkDbRepositoryFactoryBean::class,
        RethinkDbRepositoryConfigurationExtension::class
)
@Import(RethinkDbRepositoriesAutoConfigureRegistrar::class)
@AutoConfigureAfter(RethinkDbDataAutoConfiguration::class)
open class RethinkDbRepositoriesAutoConfiguration