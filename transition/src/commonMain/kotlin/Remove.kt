package com.juul.krayon.transition

import com.juul.krayon.element.Element

/**
 * Removes each selected element from its parent when its transition ends. This is the convenient way
 * to animate an element out (for example, fading or shrinking) before detaching it.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/modifying#transition_remove).
 */
public fun <E : Element, D> Transition<E, D>.remove(): Transition<E, D> {
    forEachElement { element ->
        scheduler.scheduleFor(element, id)?.addListener(TransitionEvent.End) {
            element.parent?.removeChild(element)
        }
    }
    return this
}
