package at.d4m.spring.data.rethinkdb.convert

import at.d4m.spring.data.rethinkdb.mapping.RethinkDbMappingContext
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbPersistentEntity
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbPersistentProperty
import org.springframework.data.convert.EntityConverter

/**
 * @author Christoph Muck
 */
interface RethinkDbConverter : EntityConverter<RethinkDbPersistentEntity<*>, RethinkDbPersistentProperty, Any, MutableMap<String, Any>> {
    override fun getMappingContext(): RethinkDbMappingContext
}
