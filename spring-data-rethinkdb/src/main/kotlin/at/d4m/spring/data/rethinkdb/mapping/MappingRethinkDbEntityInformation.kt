package at.d4m.spring.data.rethinkdb.mapping

import org.springframework.data.repository.core.support.PersistentEntityInformation

/**
 * @author Christoph Muck
 */
class MappingRethinkDbEntityInformation<T, ID>(private val entity: RethinkDbPersistentEntity<T>)
    : RethinkDbEntityInformation<T, ID>, PersistentEntityInformation<T, ID>(entity) {

    override val tableName: String
        get() = entity.tableName

}