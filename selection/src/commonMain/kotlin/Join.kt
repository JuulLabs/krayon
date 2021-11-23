package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.ElementBuilder

public fun <E: Element, D> UpdateSelection<E, D>.join(
    builder: ElementBuilder<E>
): Selection<E, D> = join(onEnter = { append(builder) })

public inline fun <E : Element, D> UpdateSelection<E, D>.join(
    crossinline onEnter: EnterSelection<D>.() -> Selection<E, D>,
    crossinline onUpdate: Selection<E, D>.() -> Selection<E, D> = { this },
    crossinline onExit: Selection<E, D>.() -> Unit = { remove() },
): Selection<E, D> {
    val enter = enter.onEnter()
    val update = onUpdate()
    exit.onExit()
    return enter.merge(update).order()
}
