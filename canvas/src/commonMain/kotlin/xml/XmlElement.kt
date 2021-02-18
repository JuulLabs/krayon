package com.juul.krayon.canvas.xml

private const val INDENTATION_STEP = "  "

internal class XmlElement(
    val id: Id,
) {
    constructor(id: String) : this(id.toId())

    private val attributes = mutableMapOf<Id, EscapedString>()
    private val contents = mutableListOf<Content>()

    fun setAttribute(id: Id, value: EscapedString) = apply {
        attributes[id] = value
    }

    fun setAttribute(id: String, value: String) = setAttribute(id.toId(), value.escape())
    fun setAttribute(id: String, value: Double) = setAttribute(id, value.toString())
    fun setAttribute(id: String, value: Float) = setAttribute(id, value.toDouble().toString())

    fun addContent(content: Content) = apply {
        this.contents += content
    }

    fun addContent(text: EscapedString) = addContent(Content.Text(text))
    fun addContent(child: XmlElement) = addContent(Content.Child(child))

    sealed class Content {
        data class Child(val child: XmlElement) : Content()
        data class Text(val text: EscapedString) : Content()
    }

    override fun toString(): String = toString(0)

    private fun StringBuilder.indent(depth: Int) {
        for (i in 0 until depth) {
            append(INDENTATION_STEP)
        }
    }

    private fun toString(depth: Int): String = buildString {
        indent(depth)
        append("<$id")
        for (attribute in attributes) {
            append(" ${attribute.key}=\"${attribute.value}\"")
        }
        if (contents.isEmpty()) {
            append(" />")
        } else {
            append(">")
            for (content in contents) {
                appendLine()
                when (content) {
                    is Content.Child -> append(content.child.toString(depth + 1))
                    is Content.Text -> {
                        indent(depth + 1)
                        append(content.text)
                    }
                }
            }
            appendLine()
            indent(depth)
            append("</$id>")
        }
    }
}
