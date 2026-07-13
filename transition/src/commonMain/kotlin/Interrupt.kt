package com.juul.krayon.transition

import com.juul.krayon.element.Element
import com.juul.krayon.selection.Selection

/**
 * Immediately interrupts the transition with the given [name] on each selected element, cancelling any
 * scheduled-but-not-started transition and interrupting any running one (dispatching the corresponding
 * [TransitionEvent]). Unlike creating a new transition, this stops the animation right away without a
 * final frame.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/control-flow#selection_interrupt).
 */
public fun <E : Element, D, S : Selection<E, D>> S.interrupt(name: String = ""): S {
    for (group in groups) {
        for (node in group.nodes) {
            if (node != null) node.rootOrNull()?.existingScheduler()?.interruptByName(node, name)
        }
    }
    return this
}

/** Immediately interrupts this specific transition on all of its elements. */
public fun <E : Element, D> Transition<E, D>.interrupt(): Transition<E, D> {
    forEachElement { element -> scheduler.interruptById(element, id) }
    return this
}
