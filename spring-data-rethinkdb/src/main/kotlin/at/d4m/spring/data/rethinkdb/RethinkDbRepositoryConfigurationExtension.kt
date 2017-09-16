package at.d4m.spring.data.rethinkdb

/**
 * @author Christoph Muck
 */
class RethinkDbRepositoryConfigurationExtension : AbstractRethinkDbRepositoryConfigurationExtension() {

    override fun getRepositoryFactoryBeanClassName(): String {
        return RethinkDbRepositoryFactory::class.java.name
    }
}
