package at.d4m.spring.data.rethinkdb.mapping

import org.springframework.data.mapping.context.AbstractMappingContext
import org.springframework.data.mapping.model.Property
import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.util.TypeInformation

/**
 * @author Christoph Muck
 */
open class RethinkDbMappingContext : AbstractMappingContext<BasicRethinkDbPersistentEntity<*>, RethinkDbPersistentProperty>() {

    override fun createPersistentProperty(property: Property, owner: BasicRethinkDbPersistentEntity<*>, simpleTypeHolder: SimpleTypeHolder): RethinkDbPersistentProperty {
        return BasicRethinkDbPersistentProperty(property, owner, simpleTypeHolder)
    }

    override fun <T : Any?> createPersistentEntity(typeInformation: TypeInformation<T>): BasicRethinkDbPersistentEntity<*> {
        return BasicRethinkDbPersistentEntity(typeInformation)
    }
}

