package com.juul.krayon.transition

import com.juul.krayon.element.Element
import com.juul.krayon.selection.Arguments
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.filter

/**
 * Returns a new transition containing only the elements for which [predicate] returns true.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/control-flow#transition_filter).
 */
public fun <E : Element, D> Transition<E, D>.filter(
    predicate: E.(Arguments<D, E?>) -> Boolean,
): Transition<E, D> {
    val filtered = Selection(groups).filter(predicate)
    return Transition(filtered.groups, scheduler, id, name)
}
