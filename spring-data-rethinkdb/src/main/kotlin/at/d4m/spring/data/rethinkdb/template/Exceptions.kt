package at.d4m.spring.data.rethinkdb.template

open class NotFoundException internal constructor(val name: String, val value: Any?) : RuntimeException("$name '$value' could not be found!")

class IdNotFoundException internal constructor(val id: Any?) : NotFoundException("Id", id)