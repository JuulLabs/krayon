package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas

public abstract class Element {
    public var data: Any? = null

    public open var parent: Element? = null

    private val _children: MutableList<Element> = mutableListOf()
    public val children: List<Element> = _children

    public open fun <E: Element> appendChild(child: E): E {
        child.parent?.removeChild(child)
        child.parent = this
        _children.add(child)
        return child
    }

    public open fun <E: Element> insertBefore(child: E, reference: Element?): E {
        child.parent?.removeChild(child)
        child.parent = this
        when (reference) {
            null -> _children.add(child)
            else -> when (val index = children.indexOf(reference)) {
                -1 -> _children.add(child)
                else -> _children.add(index, child)
            }
        }
        return child
    }

    public fun <E: Element> removeChild(child: E): E {
        _children.remove(child)
        return child
    }

    public abstract fun <PAINT, PATH> applyTo(canvas: Kanvas<PAINT, PATH>)
}

public val Element.descendents: Sequence<Element>
    get() = sequence {
        for (child in children) {
            yield(child)
            yieldAll(child.descendents)
        }
    }
