package com.juul.krayon.transition

import com.juul.krayon.element.Element
import com.juul.krayon.selection.Group
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.each

/**
 * A selection-like handle for animating changes to elements over time. Instead of applying changes
 * instantaneously (as [Selection.each] does), a transition interpolates each element from its current
 * state to a target state over a duration.
 *
 * Create one with [transition]. Configure timing with [duration], [delay], and [ease]; specify targets
 * with [attr] or [tween]; observe lifecycle with [on]; and remove elements when done with [remove].
 *
 * See the analogous [d3-transition](https://d3js.org/d3-transition) module.
 */
public open class Transition<E : Element, D> internal constructor(
    public val groups: List<Group<E, D>>,
    internal val scheduler: Scheduler,
    internal val id: Long,
    internal val name: String,
)

/**
 * Returns a new transition on the selected elements with the given [name]. Timing defaults to a
 * duration of 250ms, no delay, and [easeCubicInOut] easing, matching d3.
 *
 * The selected elements must be attached to a [com.juul.krayon.element.RootElement]; that root owns the
 * scheduler that drives the animation.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/selecting#selection_transition).
 */
public fun <E : Element, D> Selection<E, D>.transition(name: String = ""): Transition<E, D> {
    val root = resolveRoot() ?: error(
        "transition() requires the selected elements to be attached to a RootElement.",
    )
    val scheduler = root.scheduler()
    val id = scheduler.nextId()
    val reference = scheduler.currentTime
    each {
        scheduler.create(
            element = this,
            id = id,
            name = name,
            referenceTime = reference,
            delay = 0L,
            duration = DEFAULT_DURATION,
            ease = easeCubicInOut,
            interruptSiblings = true,
        )
    }
    return Transition(groups, scheduler, id, name)
}

/**
 * Returns a new transition on the same elements as this transition, scheduled to start when this one
 * ends. The new transition inherits this transition's name, per-element duration, and easing.
 *
 * See the analogous [d3 function](https://d3js.org/d3-transition/control-flow#transition_transition).
 */
public fun <E : Element, D> Transition<E, D>.transition(): Transition<E, D> {
    val newId = scheduler.nextId()
    forEachElement { element ->
        val parent = scheduler.scheduleFor(element, id)
        val reference = if (parent != null) parent.startTime + parent.duration else scheduler.currentTime
        val duration = parent?.duration ?: DEFAULT_DURATION
        val ease = parent?.ease ?: easeCubicInOut
        scheduler.create(
            element = element,
            id = newId,
            name = name,
            referenceTime = reference,
            delay = 0L,
            duration = duration,
            ease = ease,
            interruptSiblings = false,
        )
    }
    return Transition(groups, scheduler, newId, name)
}

/** Returns a [Selection] of the same elements, for applying non-animated operations. */
public fun <E : Element, D> Transition<E, D>.selection(): Selection<E, D> = Selection(groups)

private fun Selection<*, *>.resolveRoot() =
    groups.firstNotNullOfOrNull { group ->
        group.nodes.firstNotNullOfOrNull { node -> node?.rootOrNull() } ?: group.parent?.rootOrNull()
    }

internal inline fun <E : Element, D> Transition<E, D>.forEachElement(action: (E) -> Unit) {
    for (group in groups) {
        for (node in group.nodes) {
            if (node != null) action(node)
        }
    }
}

internal inline fun <E : Element, D> Transition<E, D>.forEachSchedule(action: (Schedule) -> Unit) {
    forEachElement { element -> scheduler.scheduleFor(element, id)?.let(action) }
}
