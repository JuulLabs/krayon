package com.juul.krayon.transition

import com.juul.krayon.element.Element
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.merge

/**
 * Returns a new transition merging this transition's groups with [other]'s, such as an enter
 * transition merged with an update transition.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/control-flow#transition_merge).
 */
public fun <E : Element, D> Transition<E, D>.merge(
    other: Transition<E, D>,
): Transition<E, D> {
    val merged = Selection(groups).merge(Selection(other.groups))
    return Transition(merged.groups, scheduler, id, name)
}
