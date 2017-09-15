package at.d4m.spring.data.rethinkdb.autoconfiguration

import at.d4m.rxrethinkdb.RethinkDBClient
import at.d4m.spring.data.rethinkdb.convert.MappingRethinkDbConverter
import at.d4m.spring.data.rethinkdb.mapping.RethinkDbMappingContext
import at.d4m.spring.data.rethinkdb.template.DefaultRethinkDbTemplateHelper
import at.d4m.spring.data.rethinkdb.template.RethinkDbTemplate
import com.rethinkdb.RethinkDB
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Christoph Muck
 */
@Configuration
@ConditionalOnClass(RethinkDB::class, RethinkDbTemplate::class)
@AutoConfigureAfter(RethinkDbAutoConfiguration::class)
open class RethinkDbDataAutoConfiguration {

    companion object {
        private const val defaultDatabaseName = "springdata"
    }

    @Bean
    @ConditionalOnMissingBean
    open fun rethinkDbTemplate(rethinkDbClient: RethinkDBClient, mappingRethinkDbConverter: MappingRethinkDbConverter): RethinkDbTemplate {
        val helper = DefaultRethinkDbTemplateHelper()
        helper.createDatabaseIfNotExists(defaultDatabaseName, rethinkDbClient)
        return RethinkDbTemplate(rethinkDbClient, mappingRethinkDbConverter, helper, defaultDatabaseName)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun mappingRethinkDbConverter(rethinkDbMappingContext: RethinkDbMappingContext): MappingRethinkDbConverter {
        return MappingRethinkDbConverter(rethinkDbMappingContext)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun rethinkDbMappingContext(): RethinkDbMappingContext {
        return RethinkDbMappingContext()
    }
}