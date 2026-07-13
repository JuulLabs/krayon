package com.juul.krayon.transition

import com.juul.krayon.element.Element
import com.juul.krayon.selection.Arguments
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.each

/**
 * Sets the per-element duration, in milliseconds, for all selected elements.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/timing#transition_duration).
 */
public fun <E : Element, D> Transition<E, D>.duration(milliseconds: Long): Transition<E, D> {
    forEachSchedule { it.duration = milliseconds }
    return this
}

/** Sets the duration per element using [value], evaluated for each element with its datum and index. */
public fun <E : Element, D> Transition<E, D>.duration(
    value: E.(Arguments<D, E?>) -> Long,
): Transition<E, D> {
    Selection(groups).each { arguments -> scheduler.scheduleFor(this, id)?.duration = value(arguments) }
    return this
}

/**
 * Sets the per-element start delay, in milliseconds, for all selected elements.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/timing#transition_delay).
 */
public fun <E : Element, D> Transition<E, D>.delay(milliseconds: Long): Transition<E, D> {
    forEachSchedule { it.delay = milliseconds }
    return this
}

/** Sets the delay per element using [value], evaluated for each element with its datum and index. */
public fun <E : Element, D> Transition<E, D>.delay(
    value: E.(Arguments<D, E?>) -> Long,
): Transition<E, D> {
    Selection(groups).each { arguments -> scheduler.scheduleFor(this, id)?.delay = value(arguments) }
    return this
}

/**
 * Sets the [Easing] function for all selected elements. Defaults to [easeCubicInOut].
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/timing#transition_ease).
 */
public fun <E : Element, D> Transition<E, D>.ease(easing: Easing): Transition<E, D> {
    forEachSchedule { it.ease = easing }
    return this
}
