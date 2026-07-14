package com.juul.krayon.transition

import com.juul.krayon.element.Element

internal const val DEFAULT_DURATION: Long = 250L

/**
 * Owns and advances all [Schedule]s for a single element tree. Deterministic by design: state only
 * changes inside [tick], so render loops and tests drive animations the same way.
 */
internal class Scheduler {

    private var nextId: Long = 0L

    /** The most recent time passed to [tick]. New schedules use this as their reference time. */
    var currentTime: Long = 0L
        private set

    /** element -> (transition id -> schedule), iterated in insertion order for deterministic ticks. */
    private val perElement: LinkedHashMap<Element, LinkedHashMap<Long, Schedule>> = LinkedHashMap()

    fun nextId(): Long = nextId++

    fun scheduleFor(element: Element, id: Long): Schedule? = perElement[element]?.get(id)

    /**
     * Registers a new [Schedule]. When [interruptSiblings] is true, existing schedules on [element]
     * with the same [name] are evicted first, matching d3's replace-by-name semantics.
     */
    fun create(
        element: Element,
        id: Long,
        name: String,
        referenceTime: Long,
        delay: Long,
        duration: Long,
        ease: Easing,
        interruptSiblings: Boolean,
    ): Schedule {
        val schedules = perElement.getOrPut(element) { LinkedHashMap() }
        if (interruptSiblings) {
            schedules.values.filter { it.name == name }.forEach(::evict)
        }
        val schedule = Schedule(element, id, name, referenceTime, delay, duration, ease)
        schedules[id] = schedule
        return schedule
    }

    /** Advances all schedules to [now]. Returns true while any schedule remains pending or running. */
    fun tick(now: Long): Boolean {
        currentTime = now
        val snapshot = perElement.values.flatMap { it.values.toList() }
        for (schedule in snapshot) {
            if (schedule.state == ScheduleState.SCHEDULED && now >= schedule.startTime) {
                schedule.start()
            }
            if (schedule.state == ScheduleState.STARTED) {
                val fraction = when {
                    schedule.duration <= 0L -> 1f
                    else -> ((now - schedule.startTime).toFloat() / schedule.duration).coerceIn(0f, 1f)
                }
                schedule.applyFrame(schedule.ease.ease(fraction))
                if (fraction >= 1f) {
                    schedule.end()
                    perElement[schedule.element]?.remove(schedule.id)
                }
            }
        }
        pruneEmpty()
        return hasPending()
    }

    fun interruptByName(element: Element, name: String) {
        val schedules = perElement[element] ?: return
        schedules.values.filter { it.name == name }.forEach(::evict)
        pruneEmpty()
    }

    fun interruptById(element: Element, id: Long) {
        perElement[element]?.get(id)?.let(::evict)
        pruneEmpty()
    }

    fun hasPending(): Boolean = perElement.values.any { it.isNotEmpty() }

    /** Removes [schedule] without completing it, firing [TransitionEvent.Interrupt] or [TransitionEvent.Cancel]. */
    private fun evict(schedule: Schedule) {
        schedule.fire(if (schedule.state == ScheduleState.STARTED) TransitionEvent.Interrupt else TransitionEvent.Cancel)
        perElement[schedule.element]?.remove(schedule.id)
    }

    private fun pruneEmpty() {
        val iterator = perElement.entries.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().value.isEmpty()) iterator.remove()
        }
    }
}
