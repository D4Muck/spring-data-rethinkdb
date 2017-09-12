package at.d4m.spring.data.rethinkdb

import com.rethinkdb.net.Connection

class RethinkDbTemplate(
        private val connection: Connection
) : RethinkDbOperations {

}