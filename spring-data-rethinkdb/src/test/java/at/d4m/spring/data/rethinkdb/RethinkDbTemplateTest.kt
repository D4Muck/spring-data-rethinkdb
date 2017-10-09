package at.d4m.spring.data.rethinkdb

import at.d4m.rxrethinkdb.Database
import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.rxrethinkdb.Table
import at.d4m.rxrethinkdb.query.Query
import at.d4m.rxrethinkdb.query.QueryResponse
import at.d4m.rxrethinkdb.query.components.changes
import at.d4m.rxrethinkdb.query.components.delete
import at.d4m.rxrethinkdb.query.components.get
import at.d4m.spring.data.rethinkdb.convert.RethinkDbConverter
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbMappingContext
import at.d4m.spring.data.rethinkdb.template.RethinkDbChange
import at.d4m.spring.data.rethinkdb.template.RethinkDbChangeEvent.*
import at.d4m.spring.data.rethinkdb.template.RethinkDbOperations
import at.d4m.spring.data.rethinkdb.template.RethinkDbTemplate
import at.d4m.spring.data.rethinkdb.template.RethinkDbTemplateHelper
import com.nhaarman.mockito_kotlin.*
import com.rethinkdb.gen.ast.ReqlExpr
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.subscribers.TestSubscriber
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id

/**
 * @author Christoph Muck
 */
internal class RethinkDbTemplateTest {

    lateinit var db: Database
    lateinit var table: Table
    lateinit var client: RethinkDBClient
    lateinit var converter: RethinkDbConverter
    lateinit var helper: RethinkDbTemplateHelper
    lateinit var mappingContext: RethinkDbMappingContext
    lateinit var template: RethinkDbOperations
    val databaseName = "testdbname"
    val tableName = "testTable"
    @BeforeEach
    fun initMocks() {
        table = mock {
            on { name } doReturn tableName
        }
        db = mock {
            on { name } doReturn databaseName
            on { getTableWithName(tableName) } doReturn table
        }
        client = mock {
            on { getDatabaseWithName(databaseName) } doReturn db
        }
        mappingContext = mock()
        converter = mock {
            on { mappingContext } doReturn mappingContext
        }
        helper = mock()
        template = RethinkDbTemplate(client, converter, helper, databaseName)
    }

    @Test
    fun testInsert() {
        val tableName = "test"
        val fakeId = "98324394579234757"
        val testObject = Any()

        whenever(helper.insertMap(tableName, db)).thenReturn(SingleTransformer { it.map { fakeId } })

        template.insert(testObject, tableName).assertComputationScheduler().test().await().assertComplete()

        verify(helper).createTableIfNotExists(tableName, db)
        verify(converter).write(eq(testObject), any())
        verify(helper).populateIdIfNecessary(testObject, fakeId, mappingContext)
    }

    private data class SomeClass(
            @field:Id
            val id: String?,
            val age: Int
    ) {
        fun toMap(): Map<String, Any> {
            return mapOf("id" to (id ?: ""), "age" to age)
        }
    }

    @Test
    fun testFind() {
        val expected = listOf(
                SomeClass("0980a800ß", 5),
                SomeClass("df78sadf7asdfadsf", 0),
                SomeClass("fasd9f87a976sdf98", 44),
                SomeClass("asfsd34234", 333)
        )
        val results: List<Map<String, Any>> = expected.map(SomeClass::toMap)
        val queryResponse = mock<QueryResponse> {
            on { responseAsFlowable() } doReturn Flowable.fromIterable(results)
        }

        whenever(table.executeQuery()).thenReturn(queryResponse)
        val entityClass = SomeClass::class.java
        mockConverterRead(expected)


        val subscriber = TestSubscriber<SomeClass>()
        template.find(entityClass = entityClass, table = tableName)
                .doOnComplete { print("Complete ${Thread.currentThread().name}") }
                .assertComputationScheduler().subscribe(subscriber)
        subscriber.await()
        subscriber.assertValueSequence(expected)
    }

    private fun mockConverterRead(entities: List<SomeClass>) {
        entities.zip(entities.map { it.toMap() }).forEach { (entity, map) ->
            whenever(converter.read(entity::class.java, map.toMutableMap())).thenReturn(entity)
        }
    }

    @Test
    fun testRemove() {
        template.remove(table = tableName).assertComputationScheduler().blockingAwait()
        verify(table).executeQuery(Query.empty<ReqlExpr>().delete())
    }

    @Test
    fun testWithIdRemove() {
        val id = "587"
        template.remove(tableName, id).assertComputationScheduler().blockingAwait()
        verify(table).executeQuery(Query.get(id).delete())
    }


    @Test
    fun testChangeFeed() {
        val expected = listOf(
                RethinkDbChange(SomeClass("0980a800ß", 5), INITIAL),
                RethinkDbChange(SomeClass("df78sadf7asdfadsf", 0), INITIAL),
                RethinkDbChange(SomeClass("fasd9f87a976sdf98", 44), CREATED),
                RethinkDbChange(SomeClass("asfsd34234", 333), DELETED),
                RethinkDbChange(SomeClass("0980a800ß", 5), CREATED)
        )

        val input = Flowable.fromIterable(expected.map { changeFeedMap(it) })
        val queryResponse: QueryResponse = mock {
            on { responseAsFlowable() } doReturn input
        }
        whenever(table.executeQuery(Query.changes())).thenReturn(queryResponse)
        mockConverterRead(expected.mapNotNull { it.value })
        val changeFeed = template.changeFeed(SomeClass::class.java, tableName).assertComputationScheduler()
        verifyZeroInteractions(db)
        Assertions.assertEquals(expected, changeFeed.toList().blockingGet())
    }

    private fun changeFeedMap(change: RethinkDbChange<SomeClass>): Map<String, Any> {
        val map = mutableMapOf<String, Any?>()
        val changeValueMap = change.value?.toMap()
        when (change.event) {
            INITIAL -> {
                map["new_val"] = changeValueMap
            }
            CREATED -> {
                map["new_val"] = changeValueMap
                map["old_val"] = null
            }
            DELETED -> {
                map["old_val"] = changeValueMap
                map["new_val"] = null
            }
        }
        @Suppress("UNCHECKED_CAST")
        return map as Map<String, Any>
    }
}
