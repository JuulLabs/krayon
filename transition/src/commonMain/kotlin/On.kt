package com.juul.krayon.transition

import com.juul.krayon.element.Element

/** See analogous [d3 function](https://d3js.org/d3-transition/control-flow#transition_on). */
public fun <E : Element, D> Transition<E, D>.on(
    event: TransitionEvent,
    listener: E.() -> Unit,
): Transition<E, D> {
    forEachElement { element -> scheduler.scheduleFor(element, id)?.addListener(event) { element.listener() } }
    return this
}
