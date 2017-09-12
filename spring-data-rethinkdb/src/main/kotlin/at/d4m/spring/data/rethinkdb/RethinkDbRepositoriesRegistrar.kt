package at.d4m.spring.data.rethinkdb

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport
import org.springframework.data.repository.config.RepositoryConfigurationExtension

class RethinkDbRepositoriesRegistrar : RepositoryBeanDefinitionRegistrarSupport() {

    override fun getExtension(): RepositoryConfigurationExtension = RethinkDbRepositoryConfigurationExtension()

    override fun getAnnotation(): Class<out Annotation> = EnableRethinkDBRepositories::class.java
}