package com.juul.krayon.element

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.juul.krayon.core.InternalKrayonApi
import com.juul.krayon.kanvas.Kanvas

@Stable
public abstract class Element {

    public abstract val tag: String

    /**
     * This is a delicate API that exposes the raw attribute backing without any type safety. This
     * is frequently used by the internals of Krayon, but is likely something you shouldn't use as
     * a library consumer. That said, it's still exposed for the cases where it's necessary, such as
     * implementing new types of element. New attributes defined via `by attributes.withDefault`
     * automatically satisfy the [Stable] contract.
     *
     * **When using this, be very careful not to clobber existing attributes.**
     */
    public val attributes: MutableMap<String, Any?> = mutableStateMapOf()

    /** Analogous to an HTML class, except you can only have one. */
    public var kind: String? by attributes.withDefault { null }

    public var data: Any? by attributes.withDefault { null }

    public open var parent: Element? by mutableStateOf(null)

    @InternalKrayonApi
    public val mutableChildren: MutableList<Element> = mutableStateListOf()
    public val children: List<Element> = mutableChildren

    public open fun <E : Element> appendChild(child: E): E {
        child.parent?.removeChild(child)
        child.parent = this
        mutableChildren.add(child)
        return child
    }

    public open fun <E : Element> insertBefore(child: E, reference: Element?): E {
        child.parent?.removeChild(child)
        child.parent = this
        when (reference) {
            null -> mutableChildren.add(child)
            else -> when (val index = children.indexOf(reference)) {
                -1 -> mutableChildren.add(child)
                else -> mutableChildren.add(index, child)
            }
        }
        return child
    }

    public fun matches(selector: ElementSelector<*>): Boolean =
        selector.trySelect(this) === this

    public open fun <E : Element> query(selector: ElementSelector<E>): E? {
        var selected = selector.trySelect(this)
        if (selected != null) return selected
        for (child in children) {
            selected = child.query(selector)
            if (selected != null) return selected
        }
        return null
    }

    public open fun <E : Element> queryAll(selector: ElementSelector<E>): Sequence<E> = sequence {
        val selected = selector.trySelect(this@Element)
        if (selected != null) yield(selected)
        for (child in children) {
            yieldAll(child.queryAll(selector))
        }
    }

    public fun <E : Element> removeChild(child: E): E {
        if (mutableChildren.remove(child)) {
            child.parent = null
        }
        return child
    }

    public abstract fun draw(kanvas: Kanvas)

    override fun toString(): String = buildString {
        append('(')
        append(tag)
        for ((key, value) in attributes) {
            append(" :$key $value")
        }
        for (child in children) {
            append(' ')
            append(child)
        }
        append(')')
    }

    public companion object : ElementSelector<Element> {
        override fun trySelect(element: Element): Element = element
    }
}
