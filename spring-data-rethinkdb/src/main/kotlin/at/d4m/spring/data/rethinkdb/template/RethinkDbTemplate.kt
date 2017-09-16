package at.d4m.spring.data.rethinkdb.template

import at.d4m.rxrethinkdb.Database
import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.rxrethinkdb.query.Query
import at.d4m.rxrethinkdb.query.components.delete
import at.d4m.rxrethinkdb.query.components.get
import at.d4m.spring.data.rethinkdb.convert.RethinkDbConverter
import com.rethinkdb.RethinkDB.r

open class RethinkDbTemplate(
        private val client: RethinkDBClient,
        override val converter: RethinkDbConverter,
        private val helper: RethinkDbTemplateHelper,
        databaseName: String
) : RethinkDbOperations {

    private val db: Database = client.getDatabaseWithName(databaseName)

    override fun save(obj: Any, table: String) {
        helper.createTableIfNotExists(table, db)

        @Suppress("UNCHECKED_CAST")
        val hashMap = r.hashMap() as MutableMap<String, Any>
        converter.write(obj, hashMap)

        val generatedId = helper.insertMap(table, hashMap, db)
        helper.populateIdIfNecessary(obj, generatedId, converter.mappingContext)
    }

    override fun <T> find(entityClass: Class<T>, table: String): List<T> {
        val unmappedEntities = db.getTableWithName(table).executeQuery().responseAsCursor().toList()
        return unmappedEntities.map { convert(entityClass, it) }
    }

    private fun <T> convert(entityClass: Class<T>, obj: Map<String, Any>): T {
        return converter.read(entityClass, obj.toMutableMap())
    }

    override fun <ID> remove(table: String, id: ID?) {
        val query = if (id != null) Query.get(id.toString()) else Query.empty()
        db.getTableWithName(table).executeQuery(query.delete())
    }

    override fun remove(table: String) {
        remove(table, null)
    }

    override fun <ID, T> findById(id: ID, entityClass: Class<T>, table: String): T? {
        val idStr = id.toString()
        val rawEntity = db.getTableWithName(table)
                .executeQuery(Query.get(idStr)).responseAsOptionalMap() ?: return null
        return convert(entityClass, rawEntity)
    }
}