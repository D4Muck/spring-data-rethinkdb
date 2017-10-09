package at.d4m.spring.data.rethinkdb.template

import at.d4m.rxrethinkdb.Database
import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.rxrethinkdb.query.Query
import at.d4m.rxrethinkdb.query.components.*
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbMappingContext
import io.reactivex.CompletableTransformer
import io.reactivex.SingleTransformer

class DefaultRethinkDbTemplateHelper : RethinkDbTemplateHelper {

    override fun createTableIfNotExists(tableName: String, db: Database) {
        val tables: List<String> = db.getTableNames()
        if (tableName !in tables) {
            db.createTableWithName(tableName)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun insertMap(tableName: String, db: Database): SingleTransformer<Map<String, Any>, String> {
        return SingleTransformer {
            it.map {
                val saveResult = db.getTableWithName(tableName).executeQuery(Query.insert(it)).responseAsMap()
                return@map (saveResult["generated_keys"] as? List<String>)?.firstOrNull()
                        ?: throw RuntimeException("Error while inserting: $saveResult")
            }
        }
    }

    override fun replaceMap(tableName: String, db: Database): SingleTransformer<Map<String, Any>, Unit> {
        return SingleTransformer {
            it.map {
                val id = it["id"] ?: throw RuntimeException("'$it' does not have an id!")
                val saveResult = db.getTableWithName(tableName).executeQuery(Query.get(id).replace(it)).responseAsMap()
                println(saveResult)
                val error = (saveResult["first_error"] as? String)
                error?.let { throw RuntimeException("Error while replacing: $error") }
                return@map Unit
            }
        }
    }

    override fun populateIdIfNecessary(obj: Any, id: String?, mappingContext: RethinkDbMappingContext) {
        id?.let {
            val entity = mappingContext.getRequiredPersistentEntity(obj::class.java)
            val idProperty = entity.idProperty
            if (idProperty != null) {
                val accessor = entity.getPropertyAccessor(obj)
                accessor.setProperty(idProperty, it)
            }
        }
    }

    override fun createDatabaseIfNotExists(name: String, client: RethinkDBClient) {
        if (name !in client.getDatabaseNames()) {
            client.createDatabaseWithName(name)
        }
    }
}