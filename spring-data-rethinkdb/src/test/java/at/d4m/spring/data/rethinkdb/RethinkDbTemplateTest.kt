package at.d4m.spring.data.rethinkdb

import at.d4m.rxrethinkdb.Database
import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.rxrethinkdb.Table
import at.d4m.spring.data.rethinkdb.convert.RethinkDbConverter
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbMappingContext
import at.d4m.spring.data.rethinkdb.template.RethinkDbOperations
import at.d4m.spring.data.rethinkdb.template.RethinkDbTemplate
import at.d4m.spring.data.rethinkdb.template.RethinkDbTemplateHelper
import com.nhaarman.mockito_kotlin.*
import com.rethinkdb.net.Cursor
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
    fun testSave() {
        val tableName = "test"
        val fakeId = "98324394579234757"
        val testObject = Any()

        whenever(helper.insertMap(eq(tableName), any(), eq(db))).thenReturn(fakeId)

        template.save(testObject, tableName)

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

        val both = expected.zip(results)

        val cursor: Cursor<Map<String, Any>> = mock {
            on { toList() } doReturn results
        }
        whenever(table.find()).thenReturn(cursor)
        val entityClass = SomeClass::class.java
        both.forEach { (entity, map) ->
            whenever(converter.read(entityClass, map.toMutableMap())).thenReturn(entity)
        }
        val values: List<SomeClass> = template.find(entityClass = entityClass, table = tableName)
        Assertions.assertEquals(values, expected)
    }

    @Test
    fun testRemove() {
        template.remove(table = tableName)
        verify(table).removeAll()
    }

}
