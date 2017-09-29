package at.d4m.spring.data.rethinkdb.mapping

import org.springframework.data.mapping.Association
import org.springframework.data.mapping.PersistentEntity
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty
import org.springframework.data.mapping.model.Property
import org.springframework.data.mapping.model.SimpleTypeHolder

/**
 * @author Christoph Muck
 */
class BasicRethinkDbPersistentProperty(
        property: Property,
        owner: PersistentEntity<*, RethinkDbPersistentProperty>,
        simpleTypeHolder: SimpleTypeHolder
) : AnnotationBasedPersistentProperty<RethinkDbPersistentProperty>(property, owner, simpleTypeHolder),
        RethinkDbPersistentProperty {

    override fun createAssociation(): Association<RethinkDbPersistentProperty> {
        return Association<RethinkDbPersistentProperty>(this, null)
    }
}