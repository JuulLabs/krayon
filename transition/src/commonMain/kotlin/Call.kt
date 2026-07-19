package com.juul.krayon.transition

import com.juul.krayon.element.Element

/**
 * Calls [block] with this transition and returns it, for fluent, reusable transition helpers.
 *
 * See the analogous [d3 function](https://d3js.org/d3-selection/control-flow#selection_call).
 */
public fun <E : Element, D> Transition<E, D>.call(
    block: (Transition<E, D>) -> Unit,
): Transition<E, D> {
    block(this)
    return this
}
