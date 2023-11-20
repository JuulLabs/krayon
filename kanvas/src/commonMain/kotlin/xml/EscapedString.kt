package com.juul.krayon.kanvas.xml

internal class EscapedString private constructor(private val string: String) {
    override fun toString(): String = string

    override fun hashCode(): Int = string.hashCode()

    override fun equals(other: Any?): Boolean = this === other || (other is EscapedString && string == other.string)

    companion object {
        fun encode(source: String): EscapedString = EscapedString(
            // Assume no escaping for starting capacity.
            buildString(capacity = source.length) {
                for (value in source) {
                    when (value) {
                        '<' -> append("&lt;")
                        '>' -> append("&gt;")
                        '&' -> append("&amp;")
                        '\'' -> append("&apos;")
                        '"' -> append("&quot;")
                        else -> append(value)
                    }
                }
            },
        )
    }
}

internal fun String.escape(): EscapedString = EscapedString.encode(this)
