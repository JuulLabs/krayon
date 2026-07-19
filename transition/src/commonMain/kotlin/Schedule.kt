package com.juul.krayon.transition

import com.juul.krayon.element.Element

internal enum class ScheduleState { SCHEDULED, STARTED, ENDED }

/**
 * The per-element state of a single transition, analogous to an entry in d3's `node.__transition`.
 * A [Transition] created from a selection registers one [Schedule] per selected element, all sharing
 * the same [id] and [name].
 */
internal class Schedule(
    val element: Element,
    val id: Long,
    val name: String,
    val referenceTime: Long,
    var delay: Long,
    var duration: Long,
    var ease: Easing,
) {
    /** Invoked once at [start] to produce per-frame appliers, so start values reflect current element state. */
    val tweens: LinkedHashMap<String, () -> ((Float) -> Unit)?> = LinkedHashMap()

    private val listeners: HashMap<TransitionEvent, MutableList<() -> Unit>> = HashMap()

    var state: ScheduleState = ScheduleState.SCHEDULED
        private set

    private var appliers: List<(Float) -> Unit> = emptyList()

    val startTime: Long get() = referenceTime + delay

    fun addListener(event: TransitionEvent, listener: () -> Unit) {
        listeners.getOrPut(event) { mutableListOf() }.add(listener)
    }

    fun fire(event: TransitionEvent) {
        listeners[event]?.forEach { it() }
    }

    fun start() {
        state = ScheduleState.STARTED
        // Matches d3: tweens initialize after the start event, so start listeners may mutate
        // attributes and have those mutations picked up as tween starting values.
        fire(TransitionEvent.Start)
        appliers = tweens.values.mapNotNull { factory -> factory() }
    }

    fun applyFrame(easedFraction: Float) {
        appliers.forEach { applier -> applier(easedFraction) }
    }

    fun end() {
        state = ScheduleState.ENDED
        fire(TransitionEvent.End)
    }
}
