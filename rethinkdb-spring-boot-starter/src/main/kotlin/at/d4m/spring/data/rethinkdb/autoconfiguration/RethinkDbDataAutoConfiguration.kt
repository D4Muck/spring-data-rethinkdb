package at.d4m.spring.data.rethinkdb.autoconfiguration

import at.d4m.spring.data.rethinkdb.RethinkDbTemplate
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
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

    @Bean
    @ConditionalOnMissingBean
    open fun rethinkDbTemplate(rethinkDbConnection: Connection): RethinkDbTemplate {
        return RethinkDbTemplate(rethinkDbConnection)
    }
}