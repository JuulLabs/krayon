package com.juul.krayon.transition

import com.juul.krayon.element.Element

/**
 * Registers a custom tween for full control over interpolation. The [factory] is invoked once per
 * element when the transition starts; it returns a per-frame function receiving the eased fraction,
 * or `null` to skip the element. Registering a tween with an existing [name] replaces it.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/modifying#transition_tween).
 */
public fun <E : Element, D> Transition<E, D>.tween(
    name: String,
    factory: E.() -> ((Float) -> Unit)?,
): Transition<E, D> {
    forEachElement { element -> scheduler.scheduleFor(element, id)?.tweens?.set(name) { element.factory() } }
    return this
}
