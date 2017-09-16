package at.d4m.spring.data.rethinkdb.reactive

import at.d4m.spring.data.rethinkdb.EnableRethinkDBRepositories
import at.d4m.spring.data.rethinkdb.RethinkDbRepositoryConfigurationExtension
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport
import org.springframework.data.repository.config.RepositoryConfigurationExtension

class ReactiveRethinkDbRepositoriesRegistrar : RepositoryBeanDefinitionRegistrarSupport() {

    override fun getExtension(): RepositoryConfigurationExtension = ReactiveRethinkDbRepositoryConfigurationExtension()

    override fun getAnnotation(): Class<out Annotation> = EnableReactiveRethinkDBRepositories::class.java
}