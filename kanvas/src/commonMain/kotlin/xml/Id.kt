package com.juul.krayon.canvas.xml

private val NUMBERS = '0'..'9'
private val ALLOWED_KEY_CHARS = (('a'..'z') + ('A'..'Z') + NUMBERS + '_' + '-').toHashSet()

internal class Id private constructor(private val string: String) {
    override fun toString(): String = string
    override fun hashCode(): Int = string.hashCode()
    override fun equals(other: Any?): Boolean = this === other || (other is Id && string == other.string)

    companion object {
        fun from(source: String): Id {
            require(source.isNotEmpty()) { "Id cannot be empty." }
            require(source[0] != '-') { "Id cannot begin with a dash (-) character." }
            require(source[0] !in NUMBERS) { "Id cannot begin with a number." }
            source.forEach { require(it in ALLOWED_KEY_CHARS) { "Illegal id character: $it" } }
            return Id(source)
        }
    }
}

internal fun String.toId(): Id = Id.from(this)
