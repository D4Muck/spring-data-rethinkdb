package at.d4m.spring.data.rethinkdb.template

import at.d4m.rxrethinkdb.Database
import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.rxrethinkdb.query.Query
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

        val hashMap = r.hashMap() as MutableMap<String, Any>
        converter.write(obj, hashMap)

        val generatedId = helper.insertMap(table, hashMap, db)
        helper.populateIdIfNecessary(obj, generatedId, converter.mappingContext)
    }

    override fun <T> find(entityClass: Class<T>, table: String): List<T> {
        val unmappedEntities = db.getTableWithName(table).find().toList()
        return unmappedEntities.map { convert(entityClass, it) }
    }

    override fun remove(table: String) {
        db.getTableWithName(table).removeAll()
    }

    override fun <ID, T> findById(id: ID, entityClass: Class<T>, table: String): T? {
        val idStr = id.toString()
        val rawEntity = db.getTableWithName(table).findOne(Query.get(idStr)) ?: return null
        return convert(entityClass, rawEntity)
    }

    private fun <T> convert(entityClass: Class<T>, obj: Map<String, Any>): T {
        return converter.read(entityClass, obj.toMutableMap())
    }
}