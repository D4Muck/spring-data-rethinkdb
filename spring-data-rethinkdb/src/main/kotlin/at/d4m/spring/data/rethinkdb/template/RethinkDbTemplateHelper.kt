package at.d4m.spring.data.rethinkdb.template

import at.d4m.rxrethinkdb.Database
import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbMappingContext
import io.reactivex.Single
import io.reactivex.SingleTransformer

/**
 * @author Christoph Muck
 */
interface RethinkDbTemplateHelper {

    fun createTableIfNotExists(tableName: String, db: Database)
    fun insertMap(tableName: String, db: Database): SingleTransformer<Map<String, Any>, String>
    fun populateIdIfNecessary(obj: Any, id: String?, mappingContext: RethinkDbMappingContext)
    fun createDatabaseIfNotExists(name: String, client: RethinkDBClient)
    fun replaceMap(tableName: String, db: Database): SingleTransformer<Map<String, Any>, Unit>
}