package at.d4m.spring.data.rethinkdb.convert

import at.d4m.spring.data.rethinkdb.mapping.RethinkDbMappingContext
import at.d4m.spring.data.rethinkdb.toMap
import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id
import org.springframework.data.mapping.model.MappingInstantiationException

/**
 * @author Christoph Muck
 */
internal class MappingRethinkDbConverterTest {

    lateinit var mappingContext: RethinkDbMappingContext

    lateinit var converter: MappingRethinkDbConverter

    @BeforeEach
    fun initMocks() {
        mappingContext = spy(RethinkDbMappingContext())
        converter = MappingRethinkDbConverter(mappingContext)
    }

    data class DataClassToRead(
            @field:Id
            val id: String?,
            val age: Int
    )

    @Test
    fun testReadDataClassConstructorParamNotAvailable() {
        val id = "1234908719-12390487132"
        Assertions.assertThrows(MappingInstantiationException::class.java) {
            testReadClass(DataClassToRead(id, 0), mutableMapOf("id" to id))
        }
    }

    @Test
    fun testReadDataClass() {
        testReadClass(DataClassToRead("1234908719-12390487132", 56))
    }

    private fun testReadClass(expected: Any, input: MutableMap<String, Any> = expected.toMap()) {
        val entity = mappingContext.getRequiredPersistentEntity(expected::class.java)
        val entitySpy = spy(entity)

        val verifications = mutableListOf<() -> Unit>()

        doAnswer {
            val accessor = spy(entity.getPropertyAccessor(it.arguments[0]))
            for (property in entity) {
                verifications.add {
                    val numInvocations = if (entity.isConstructorArgument(property)) 0 else 1
                    verify(accessor, times(numInvocations)).setProperty(eq(property), any())
                }
            }
            accessor
        }.whenever(entitySpy).getPropertyAccessor(any())

        doReturn(entitySpy).whenever(mappingContext).getRequiredPersistentEntity(expected::class.java)

        val actual = converter.read(expected::class.java, input)
        Assertions.assertEquals(expected, actual)
        verifications.forEach { it() }
    }

    class DataRead {
        @field:Id
        var id: String? = null
        var age: Int = 0

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DataRead

            if (id != other.id) return false
            if (age != other.age) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id?.hashCode() ?: 0
            result = 31 * result + age
            return result
        }

        override fun toString(): String {
            return "DataRead(id=$id, age=$age)"
        }
    }

    @Test
    fun testReadClassWNoConstructor() {
        val expected = DataRead()
        expected.id = "23486-432509-234508"
        expected.age = 56

        testReadClass(expected)
    }

    class DataRead123(
            @field:Id
            var id: String?,
            var age: Int
    ) {
        constructor() : this(null, 0)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DataRead123

            if (id != other.id) return false
            if (age != other.age) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id?.hashCode() ?: 0
            result = 31 * result + age
            return result
        }

        override fun toString(): String {
            return "DataRead123(id=$id, age=$age)"
        }
    }

    @Test
    fun testReadClassWNoEmptyConstructor() {
        val expected = DataRead123()
        expected.id = "23486-432509-234508"
        expected.age = 56

        testReadClass(expected)
    }
}