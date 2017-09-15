package at.d4m.spring.data.rethinkdb.convert

import at.d4m.spring.data.rethinkdb.mapping.RethinkDbMappingContext
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbPersistentEntity
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbPersistentProperty
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.core.convert.support.GenericConversionService
import org.springframework.data.convert.EntityInstantiators
import org.springframework.data.mapping.model.ConvertingPropertyAccessor
import org.springframework.data.mapping.model.PersistentEntityParameterValueProvider
import org.springframework.data.mapping.model.PropertyValueProvider
import org.springframework.util.ClassUtils

class MappingRethinkDbConverter(
        private val mappingContext: RethinkDbMappingContext
) : RethinkDbConverter {

    private val conversionService: GenericConversionService = DefaultConversionService()
    private val instantiators = EntityInstantiators()

    override fun getMappingContext(): RethinkDbMappingContext {
        return mappingContext
    }

    override fun getConversionService() = conversionService

    override fun write(source: Any, sink: MutableMap<String, Any>) {
        val entityType = ClassUtils.getUserClass(source)
        val entity = mappingContext.getRequiredPersistentEntity(entityType)
        val accessor = entity.getPropertyAccessor(source)

        for (property in entity) {

            if (!property.isWritable) {
                continue
            }

            val value = accessor.getProperty(property) ?: continue
            sink.put(property.name, value)
        }
    }

    override fun <R : Any> read(type: Class<R>, source: MutableMap<String, Any>): R {
        val entity = mappingContext.getRequiredPersistentEntity(type) as RethinkDbPersistentEntity<R>
        val instantiator = instantiators.getInstantiatorFor(entity)
        val propertyProvider = RethinkDbPropertyValueProvider(source)
        val parameterProvider = PersistentEntityParameterValueProvider(entity, propertyProvider, null)
        val instance = instantiator.createInstance(entity, parameterProvider)
        val accessor = ConvertingPropertyAccessor(entity.getPropertyAccessor(instance), conversionService)

        for (property in entity) {
            if (entity.isConstructorArgument(property)) continue
            val rawPropertyValue = source[property.name]
            accessor.setProperty(property, rawPropertyValue)
        }

        return instance
    }

    private inner class RethinkDbPropertyValueProvider(val properties: Map<String, Any>) : PropertyValueProvider<RethinkDbPersistentProperty> {
        override fun <T : Any?> getPropertyValue(property: RethinkDbPersistentProperty): T? {
            val rawValue = properties[property.name] ?: return null
            val value = conversionService.convert(rawValue, property.typeInformation.type)
            return value as T
        }
    }

}