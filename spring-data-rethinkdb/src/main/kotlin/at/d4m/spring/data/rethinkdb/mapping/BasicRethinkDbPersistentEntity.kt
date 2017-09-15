package at.d4m.spring.data.rethinkdb.mapping

import org.springframework.data.mapping.model.BasicPersistentEntity
import org.springframework.data.util.TypeInformation

/**
 * @author Christoph Muck
 */
open class BasicRethinkDbPersistentEntity<T>(typeInformation: TypeInformation<T>) :
        RethinkDbPersistentEntity<T>, BasicPersistentEntity<T, RethinkDbPersistentProperty>(typeInformation) {

    override val tableName: String = typeInformation.type.simpleName.decapitalize()
}