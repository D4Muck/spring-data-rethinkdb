package at.d4m.spring.data.rethinkdb.template

import at.d4m.rxrethinkdb.Database
import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.rxrethinkdb.query.Query
import at.d4m.rxrethinkdb.query.components.changes
import at.d4m.rxrethinkdb.query.components.delete
import at.d4m.rxrethinkdb.query.components.get
import at.d4m.spring.data.rethinkdb.convert.RethinkDbConverter
import com.rethinkdb.RethinkDB.r
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers

open class RethinkDbTemplate(
        private val client: RethinkDBClient,
        override val converter: RethinkDbConverter,
        private val helper: RethinkDbTemplateHelper,
        databaseName: String
) : RethinkDbOperations {

    private val db: Database = client.getDatabaseWithName(databaseName)

    override fun save(obj: Any, table: String): Completable {
        return Completable.defer {
            helper.createTableIfNotExists(table, db)

            @Suppress("UNCHECKED_CAST")
            val hashMap = r.hashMap() as MutableMap<String, Any>
            converter.write(obj, hashMap)

            val generatedId = helper.insertMap(table, hashMap, db)
            helper.populateIdIfNecessary(obj, generatedId, converter.mappingContext)
            Completable.complete()
        }.subscribeOn(Schedulers.io())
    }

    override fun <T> find(entityClass: Class<T>, table: String): Flowable<T> {
        return Flowable.defer { db.getTableWithName(table).executeQuery().responseAsFlowable() }
                .map { convert(entityClass, it) }.subscribeOn(Schedulers.io())
    }

    private fun <T> convert(entityClass: Class<T>, obj: Map<String, Any>): T {
        return converter.read(entityClass, obj.toMutableMap())
    }

    override fun <ID> remove(table: String, id: ID?): Completable {
        return Completable.defer {
            val query = if (id != null) Query.get(id.toString()) else Query.empty()
            db.getTableWithName(table).executeQuery(query.delete())
            Completable.complete()
        }.subscribeOn(Schedulers.io())
    }

    override fun remove(table: String): Completable {
        return remove(table, null)
    }

    override fun <ID, T> findById(id: ID, entityClass: Class<T>, table: String): Maybe<T> {
        return Maybe.defer<Map<String, Any>> {
            val idStr = id.toString()
            val rawEntity = db.getTableWithName(table).executeQuery(Query.get(idStr)).responseAsOptionalMap()
            if (rawEntity != null) {
                Maybe.just(rawEntity)
            } else {
                Maybe.empty()
            }
        }.map { convert(entityClass, it) }.subscribeOn(Schedulers.io())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> changeFeed(entityClass: Class<T>, table: String): Flowable<Change<T>> {
        return Flowable.defer {
            println("Deferred ${Thread.currentThread().name}")
            db.getTableWithName(table).executeQuery(Query.changes()).responseAsFlowable()
        }.map {
            val newVal = it["new_val"] as Map<String, Any>?
            val oldVal = it["old_val"] as Map<String, Any>?
            when {
                newVal != null && !it.containsKey("old_val") -> Change(convert(entityClass, newVal), ChangeEvent.INITIAL)
                newVal != null -> Change(convert(entityClass, newVal), ChangeEvent.CREATED)
                oldVal != null -> Change(convert(entityClass, oldVal), ChangeEvent.DELETED)
                else -> throw RuntimeException()
            }
        }.subscribeOn(Schedulers.io())
    }
}