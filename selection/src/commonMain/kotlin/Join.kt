package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.ElementBuilder

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_join). */
public fun <E : Element, D> UpdateSelection<E, D>.join(
    builder: ElementBuilder<E>,
): Selection<E, D> = join { append(builder) }

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_join). */
public inline fun <E : Element, D> UpdateSelection<E, D>.join(
    crossinline onUpdate: Selection<E, D>.() -> Selection<E, D> = { this },
    crossinline onExit: Selection<E, D>.() -> Unit = { remove() },
    crossinline onEnter: EnterSelection<D>.() -> Selection<E, D>,
): Selection<E, D> {
    val enter = enter.onEnter()
    val update = onUpdate()
    exit.onExit()
    return enter.merge(update).order()
}
