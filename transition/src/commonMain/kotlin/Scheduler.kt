package com.juul.krayon.transition

import com.juul.krayon.element.Element

internal const val DEFAULT_DURATION: Long = 250L

/**
 * Owns and advances all [Schedule]s for a single element tree (one per [com.juul.krayon.element.RootElement]).
 * The scheduler is deterministic: it only advances when [tick] is called with a monotonic time, which
 * makes both the render-loop integration and unit testing straightforward.
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
     * Registers a new [Schedule]. When [interruptSiblings] is true (the default for a freshly created
     * transition), any existing schedule on [element] with the same [name] is interrupted (if running)
     * or cancelled (if merely pending), matching d3's replace-by-name semantics.
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
            val victims = schedules.values.filter { it.name == name && it.id != id }
            for (victim in victims) {
                victim.fire(if (victim.state == ScheduleState.STARTED) TransitionEvent.Interrupt else TransitionEvent.Cancel)
                schedules.remove(victim.id)
            }
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
                    remove(schedule)
                }
            }
        }
        pruneEmpty()
        return hasPending()
    }

    fun interruptByName(element: Element, name: String) {
        val schedules = perElement[element] ?: return
        val victims = schedules.values.filter { it.name == name }
        for (victim in victims) {
            victim.fire(if (victim.state == ScheduleState.STARTED) TransitionEvent.Interrupt else TransitionEvent.Cancel)
            schedules.remove(victim.id)
        }
        pruneEmpty()
    }

    fun interruptById(element: Element, id: Long) {
        val schedules = perElement[element] ?: return
        val victim = schedules[id] ?: return
        victim.fire(if (victim.state == ScheduleState.STARTED) TransitionEvent.Interrupt else TransitionEvent.Cancel)
        schedules.remove(id)
        pruneEmpty()
    }

    fun hasPending(): Boolean = perElement.values.any { it.isNotEmpty() }

    private fun remove(schedule: Schedule) {
        perElement[schedule.element]?.remove(schedule.id)
    }

    private fun pruneEmpty() {
        val iterator = perElement.entries.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().value.isEmpty()) iterator.remove()
        }
    }
}
