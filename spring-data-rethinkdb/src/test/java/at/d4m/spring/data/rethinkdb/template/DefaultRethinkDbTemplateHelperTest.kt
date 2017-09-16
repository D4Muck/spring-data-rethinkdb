package at.d4m.spring.data.rethinkdb.template

import at.d4m.rxrethinkdb.Database
import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.rxrethinkdb.Table
import at.d4m.rxrethinkdb.query.DefaultQueryResponse
import at.d4m.rxrethinkdb.query.Query
import at.d4m.rxrethinkdb.query.components.insert
import at.d4m.spring.data.rethinkdb.mapping.BasicRethinkDbPersistentEntity
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbMappingContext
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbPersistentProperty
import com.nhaarman.mockito_kotlin.*
import com.rethinkdb.model.MapObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.mapping.PersistentPropertyAccessor

/**
 * @author Christoph Muck
 */
internal class DefaultRethinkDbTemplateHelperTest {


    private lateinit var mappingContext: RethinkDbMappingContext

    private lateinit var client: RethinkDBClient
    private lateinit var db: Database
    private lateinit var table: Table
    private val testTableName = "testTable"

    private val helper = DefaultRethinkDbTemplateHelper()

    @BeforeEach
    fun initMocks() {
        client = mock()
        table = mock {
            on { name } doReturn testTableName
        }
        db = mock {
            on { getTableWithName(testTableName) } doReturn table
        }
        mappingContext = mock()
    }

    @Test
    internal fun populateIdIfNecessaryTest() {
        val obj = TestClass()
        val id = "430958723045"

        val idPropertyMock: RethinkDbPersistentProperty = mock {
        }

        val accessor: PersistentPropertyAccessor = mock {

        }

        val entity: BasicRethinkDbPersistentEntity<TestClass> = mock {
            on { idProperty } doReturn idPropertyMock
            on { getPropertyAccessor(obj) } doReturn accessor
        }

        whenever(mappingContext.getRequiredPersistentEntity(TestClass::class.java)).thenReturn(entity)

        helper.populateIdIfNecessary(obj, id, mappingContext)
        verify(accessor).setProperty(idPropertyMock, id)
    }

    private class TestClass

    @Test
    internal fun populateIdIfNecessaryNullTest() {
        val obj = TestClass()
        val id: String? = null

        helper.populateIdIfNecessary(obj, id, mappingContext)
        verifyZeroInteractions(mappingContext)
    }

    @Test
    internal fun testCreateTableIfNotExists() {
        val tableName = "test"
        val tableList = listOf(tableName, "fk", "adfsdasdf", "asdfdfs")
        whenever(db.getTableNames()).thenReturn(tableList)
        helper.createTableIfNotExists(tableName, db)
        verify(db, times(0)).getTableWithName(any())
    }

    @Test
    internal fun testCreateTable() {
        val tableName = "test"
        val tableList = listOf("fk", "adfsdasdf", "asdfdfs")
        whenever(db.getTableNames()).thenReturn(tableList)
        helper.createTableIfNotExists(tableName, db)
        verify(db).createTableWithName(tableName)
    }

    @Test
    internal fun testSaveMapWithId() {
        val id = "2q59sdalkfhskadjf87"
        val idList = listOf(id)
        val result = mapOf("generated_keys" to idList)
        val map = MapObject().with("test", "yay") as MutableMap<String, Any>
        whenever(table.executeQuery(any())).thenReturn(DefaultQueryResponse(result))

        val actualId = helper.insertMap(testTableName, map, db)
        Assertions.assertEquals(id, actualId)
    }

    @Test
    internal fun createDatabaseIfNotExists() {
        val dbName = "testDb"
        val dbList = listOf("asg", "dasfewr")
        whenever(client.getDatabaseNames()).thenReturn(dbList)
        helper.createDatabaseIfNotExists(dbName, client)
        verify(client).createDatabaseWithName(dbName)
    }

    @Test
    internal fun createDatabaseIfNotExists123() {
        val dbName = "testDb"
        val dbList = listOf(dbName, "asg", "dasfewr")
        whenever(client.getDatabaseNames()).thenReturn(dbList)
        helper.createDatabaseIfNotExists(dbName, client)
        verify(client, times(0)).createDatabaseWithName(any())
    }
}