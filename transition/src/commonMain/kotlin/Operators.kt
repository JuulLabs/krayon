package com.juul.krayon.transition

import com.juul.krayon.element.Element
import com.juul.krayon.selection.Arguments
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.each
import com.juul.krayon.selection.filter
import com.juul.krayon.selection.merge

/**
 * Invokes [action] for each selected element (with the element as receiver and its datum/index/data),
 * applying changes immediately rather than animating them. Useful for setting non-animated attributes
 * within a transition chain.
 *
 * See the analogous [d3 function](https://d3js.org/d3-selection/control-flow#selection_each).
 */
public fun <E : Element, D> Transition<E, D>.each(
    action: E.(Arguments<D, E?>) -> Unit,
): Transition<E, D> {
    Selection(groups).each(action)
    return this
}

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

/**
 * Returns a new transition, on the same scheduler, containing only the elements for which [predicate]
 * returns true.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/control-flow#transition_filter).
 */
public fun <E : Element, D> Transition<E, D>.filter(
    predicate: E.(Arguments<D, E?>) -> Boolean,
): Transition<E, D> {
    val filtered = Selection(groups).filter(predicate)
    return Transition(filtered.groups, scheduler, id, name)
}

/**
 * Returns a new transition merging this transition's groups with [other]'s. Both transitions must share
 * the same id (for example, an enter transition merged with an update transition).
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/control-flow#transition_merge).
 */
public fun <E : Element, D> Transition<E, D>.merge(
    other: Transition<E, D>,
): Transition<E, D> {
    val merged = Selection(groups).merge(Selection(other.groups))
    return Transition(merged.groups, scheduler, id, name)
}
