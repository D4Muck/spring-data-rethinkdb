package at.d4m.spring.data.rethinkdb.template

import at.d4m.rxrethinkdb.Database
import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.rxrethinkdb.query.Query
import at.d4m.rxrethinkdb.query.components.changes
import at.d4m.rxrethinkdb.query.components.delete
import at.d4m.rxrethinkdb.query.components.get
import at.d4m.spring.data.rethinkdb.convert.RethinkDbConverter
import at.d4m.spring.data.rethinkdb.template.RethinkDbChangeEvent.*
import com.rethinkdb.RethinkDB.r
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

open class RethinkDbTemplate(
        private val client: RethinkDBClient,
        override val converter: RethinkDbConverter,
        private val helper: RethinkDbTemplateHelper,
        databaseName: String
) : RethinkDbOperations {

    private val db: Database = client.getDatabaseWithName(databaseName)

    override fun insert(obj: Any, table: String): Completable {
        return Completable.fromAction {
            helper.createTableIfNotExists(table, db)
        }
                .toSingle {
                    @Suppress("UNCHECKED_CAST")
                    val hashMap = r.hashMap() as MutableMap<String, Any>
                    converter.write(obj, hashMap)
                    hashMap
                }
                .compose(helper.insertMap(table, db))
                .map { helper.populateIdIfNecessary(obj, it, converter.mappingContext) }
                .toCompletable()
    }

    override fun replace(obj: Any, table: String): Completable {
        return Completable.fromAction { helper.createTableIfNotExists(table, db) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .toSingle {
                    @Suppress("UNCHECKED_CAST")
                    val hashMap = r.hashMap() as MutableMap<String, Any>
                    converter.write(obj, hashMap)
                    hashMap
                }
                .observeOn(Schedulers.io())
                .compose(helper.replaceMap(table, db))
                .observeOn(Schedulers.computation())
//                .map { helper.populateIdIfNecessary(obj, it, converter.mappingContext) }ñ
                .toCompletable()
    }

    override fun <T> find(entityClass: Class<T>, table: String): Flowable<T> {
        return Flowable.defer { db.getTableWithName(table).executeQuery().responseAsFlowable() }
                .subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).map { convert(entityClass, it) }
    }

    private fun <T> convert(entityClass: Class<T>, obj: Map<String, Any>): T {
        return converter.read(entityClass, obj.toMutableMap())
    }

    override fun <ID> remove(table: String, id: ID?): Completable {
        return Completable.fromAction {
            val query = if (id != null) Query.get(id.toString()) else Query.empty()
            val result = db.getTableWithName(table).executeQuery(query.delete()).responseAsMap()
            (result["skipped"] as? Long)?.let {
                if (it > 0) {
                    throw IdNotFoundException(id)
                }
            }
        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation())
    }

    override fun remove(table: String): Completable {
        return remove(table, null)
    }

    override fun <ID, T> findById(id: ID, entityClass: Class<T>, table: String): Maybe<T> {
        return Maybe.fromCallable<Map<String, Any>> {
            val idStr = id.toString()
            db.getTableWithName(table).executeQuery(Query.get(idStr)).responseAsOptionalMap()
        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).map { convert(entityClass, it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> changeFeed(entityClass: Class<T>, table: String): Flowable<RethinkDbChange<T>> {
        return Flowable.defer {
            db.getTableWithName(table).executeQuery(Query.changes()).responseAsFlowable()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map {
                    val type = it["type"] as String

                    val newVal = it["new_val"] as Map<String, Any>?
                    val oldVal = it["old_val"] as Map<String, Any>?

                    when (type) {
                        "add" -> RethinkDbChange(convert(entityClass, newVal!!), CREATED)
                        "remove" -> RethinkDbChange(convert(entityClass, oldVal!!), DELETED)
                        "change" -> RethinkDbChange(convert(entityClass, newVal!!), UPDATED)
                        "initial" -> RethinkDbChange(convert(entityClass, newVal!!), INITIAL)
                        else -> throw RuntimeException("Unrecognized state: $type")
                    }
                }
    }
}