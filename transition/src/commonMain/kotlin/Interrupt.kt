package com.juul.krayon.transition

import com.juul.krayon.element.Element
import com.juul.krayon.selection.Selection

/**
 * Immediately stops the transition with the given [name] on each selected element, dispatching
 * [TransitionEvent.Interrupt] if it was running or [TransitionEvent.Cancel] if it was still pending.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/control-flow#selection_interrupt).
 */
public fun <E : Element, D, S : Selection<E, D>> S.interrupt(name: String = ""): S {
    for (group in groups) {
        for (node in group.nodes) {
            node?.rootOrNull()?.existingScheduler()?.interruptByName(node, name)
        }
    }
    return this
}

/** Immediately stops this transition on all of its elements. */
public fun <E : Element, D> Transition<E, D>.interrupt(): Transition<E, D> {
    forEachElement { element -> scheduler.interruptById(element, id) }
    return this
}
