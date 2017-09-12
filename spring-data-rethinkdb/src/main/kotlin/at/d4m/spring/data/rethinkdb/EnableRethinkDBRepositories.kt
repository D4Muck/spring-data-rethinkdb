package at.d4m.spring.data.rethinkdb

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Import(RethinkDbRepositoriesRegistrar::class)
annotation class EnableRethinkDBRepositories(
        val includeFilters: Array<ComponentScan.Filter> = emptyArray(),
        val excludeFilters: Array<ComponentScan.Filter> = emptyArray(),
        val value: Array<String> = emptyArray(),
        val basePackages: Array<String> = emptyArray(),
        val basePackageClasses: Array<KClass<*>> = emptyArray(),
        val repositoryFactoryBeanClass: KClass<*> = RethinkDbRepositoryFactoryBean::class,
        val namedQueriesLocation: String = "",
        val repositoryImplementationPostfix: String = "Impl",
        val rethinkDbTemplateRef: String = "rethinkDbTemplate"
)