package com.juul.krayon.transition

import com.juul.krayon.element.Element
import com.juul.krayon.element.RootElement

private const val SCHEDULER_KEY: String = "com.juul.krayon.transition.scheduler"

/** Gets, or lazily creates, the [Scheduler] associated with this element tree. */
internal fun RootElement.scheduler(): Scheduler {
    val existing = attributes[SCHEDULER_KEY]
    if (existing is Scheduler) return existing
    return Scheduler().also { attributes[SCHEDULER_KEY] = it }
}

/** Returns the already-created [Scheduler] for this tree, if any, without creating one. */
internal fun RootElement.existingScheduler(): Scheduler? = attributes[SCHEDULER_KEY] as? Scheduler

/** Walks up the parent chain to find the [RootElement] this element is attached to, if any. */
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
 * Platform render loops call this each frame while [hasPendingTransitions] is true. It is also the
 * seam used by tests to drive transitions deterministically.
 */
public fun RootElement.tickTransitions(now: Long): Boolean = scheduler().tick(now)

/** True when this element tree has at least one pending or running transition. */
public val RootElement.hasPendingTransitions: Boolean
    get() = existingScheduler()?.hasPending() ?: false
