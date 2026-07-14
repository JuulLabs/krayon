package com.juul.krayon.transition

import com.juul.krayon.element.Element
import com.juul.krayon.selection.Arguments
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.each

/**
 * Invokes [action] for each selected element, applying changes immediately rather than animating them.
 *
 * See the analogous [d3 function](https://d3js.org/d3-selection/control-flow#selection_each).
 */
public fun <E : Element, D> Transition<E, D>.each(
    action: E.(Arguments<D, E?>) -> Unit,
): Transition<E, D> {
    Selection(groups).each(action)
    return this
}
