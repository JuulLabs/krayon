package com.juul.krayon.transition

import com.juul.krayon.element.Element

/**
 * Registers a custom tween named [name] for full control over interpolation. The [factory] is invoked
 * once per element when the transition starts (with the element as receiver, so its current state and
 * `data` are available); it returns a per-frame function that receives the eased fraction in `0f..1f`,
 * or `null` to skip the element. Registering a tween with an existing name replaces it.
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
