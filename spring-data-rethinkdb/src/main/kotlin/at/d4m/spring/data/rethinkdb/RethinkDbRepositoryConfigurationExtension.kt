package at.d4m.spring.data.rethinkdb

import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.data.config.ParsingUtils
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource

/**
 * @author Christoph Muck
 */

class RethinkDbRepositoryConfigurationExtension : RepositoryConfigurationExtensionSupport() {

    override fun getModulePrefix(): String = "rethinkdb"

    override fun getRepositoryFactoryBeanClassName(): String {
        return RethinkDbRepositoryFactory::class.java.name
    }

    override fun postProcess(builder: BeanDefinitionBuilder, config: XmlRepositoryConfigurationSource) {
        val element = config.element
        ParsingUtils.setPropertyReference(builder, element, "rethink-db-template-ref", "rethinkDbOperations")
    }

    override fun postProcess(builder: BeanDefinitionBuilder, config: AnnotationRepositoryConfigurationSource) {
        val attributes = config.attributes
        builder.addPropertyReference("rethinkDbOperations", attributes.getString("rethinkDbTemplateRef"))
    }
}
