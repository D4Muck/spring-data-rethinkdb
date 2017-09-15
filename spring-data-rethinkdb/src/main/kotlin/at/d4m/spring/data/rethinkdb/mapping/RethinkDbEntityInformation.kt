package at.d4m.spring.data.rethinkdb.mapping

import org.springframework.data.repository.core.EntityInformation

/**
 * @author Christoph Muck
 */
interface RethinkDbEntityInformation<T, ID> : EntityInformation<T, ID> {

    val tableName: String
}