package at.d4m.spring.data.rethinkdb.autoconfiguration

import at.d4m.rxrethinkdb.RethinkDBClient
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy

/**
 * @author Christoph Muck
 */
@Configuration
@ConditionalOnClass(RethinkDB::class)
open class RethinkDbAutoConfiguration {

    private var connection: Connection? = null

    @Bean
    @ConditionalOnMissingBean
    open fun rethinkDbHostname(): String = "localhost"

    @Bean
    @ConditionalOnMissingBean
    open fun rethinkDbConnection(rethinkDbHostname: String): Connection {
        val connection = RethinkDB.r.connection()
                .hostname(rethinkDbHostname)
                .connect()
        this.connection = connection
        return connection
    }

    @Bean
    @ConditionalOnMissingBean
    open fun rethinkDbClient(rethinkDbConnection: Connection): RethinkDBClient {
        return RethinkDBClient.create(rethinkDbConnection)
    }

    @PreDestroy
    fun close() {
        connection?.close()
    }
}