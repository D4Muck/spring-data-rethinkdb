package at.d4m.spring.data.rethinkdb.autoconfiguration.reactive

import at.d4m.spring.data.rethinkdb.autoconfiguration.RethinkDbDataAutoConfiguration
import at.d4m.spring.data.rethinkdb.reactive.ReactiveRethinkDbRepositoryConfigurationExtension
import at.d4m.spring.data.rethinkdb.reactive.ReactiveRethinkDbRepositoryFactoryBean
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author Christoph Muck
 */
@Configuration
@ConditionalOnMissingBean(
        ReactiveRethinkDbRepositoryFactoryBean::class,
        ReactiveRethinkDbRepositoryConfigurationExtension::class
)
@Import(RethinkDbReactiveRepositoriesAutoConfigureRegistrar::class)
@AutoConfigureAfter(RethinkDbDataAutoConfiguration::class)
open class RethinkDbReactiveRepositoriesAutoConfiguration