package at.d4m.spring.data.rethinkdb.template

import at.d4m.rxrethinkdb.Database
import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.rxrethinkdb.query.Query
import at.d4m.rxrethinkdb.query.components.insert
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbMappingContext

class DefaultRethinkDbTemplateHelper : RethinkDbTemplateHelper {

    override fun createTableIfNotExists(tableName: String, db: Database) {
        val tables: List<String> = db.getTableNames()
        if (tableName !in tables) {
            db.createTableWithName(tableName)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun insertMap(tableName: String, map: Map<String, Any>, db: Database): String? {
        val saveResult = db.getTableWithName(tableName).executeQuery(Query.insert(map)).responseAsMap()
        return (saveResult["generated_keys"] as? List<String>)?.firstOrNull()
    }

    override fun populateIdIfNecessary(obj: Any, id: String?, mappingContext: RethinkDbMappingContext) {
        id?.let {
            val entity = mappingContext.getRequiredPersistentEntity(obj::class.java)
            val idProperty = entity.idProperty
            val accessor = entity.getPropertyAccessor(obj)
            accessor.setProperty(idProperty, it)
        }
    }

    override fun createDatabaseIfNotExists(name: String, client: RethinkDBClient) {
        if (name !in client.getDatabaseNames()) {
            client.createDatabaseWithName(name)
        }
    }
}