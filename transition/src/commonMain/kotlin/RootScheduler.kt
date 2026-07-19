package com.juul.krayon.transition

import com.juul.krayon.element.Element
import com.juul.krayon.element.RootElement

// Storing the scheduler in the root's attribute map ties its lifecycle to the element tree, so it is
// discarded along with the tree (e.g. on resize) without any explicit cleanup hook.
private const val SCHEDULER_KEY: String = "com.juul.krayon.transition.scheduler"

internal fun RootElement.scheduler(): Scheduler {
    val existing = attributes[SCHEDULER_KEY]
    if (existing is Scheduler) return existing
    return Scheduler().also { attributes[SCHEDULER_KEY] = it }
}

internal fun RootElement.existingScheduler(): Scheduler? = attributes[SCHEDULER_KEY] as? Scheduler

internal fun Element.rootOrNull(): RootElement? {
    var current: Element? = this
    while (current != null) {
        if (current is RootElement) return current
        current = current.parent
    }
    return null
}

/**
 * Advances every active transition in this element tree to [now] (monotonic milliseconds), applying
 * interpolated attribute values to elements. Returns `true` while any transition is still pending or
 * running, signalling that another frame should be drawn.
 *
 * The built-in `ElementView`s call this each frame, so you usually won't need to call it yourself.
 */
public fun RootElement.tickTransitions(now: Long): Boolean = scheduler().tick(now)

/** True when this element tree has at least one pending or running transition. */
public val RootElement.hasPendingTransitions: Boolean
    get() = existingScheduler()?.hasPending() ?: false
