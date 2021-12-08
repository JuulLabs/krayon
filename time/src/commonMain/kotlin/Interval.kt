package com.juul.krayon.time

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime

private val UNIX_ZERO = Instant.fromEpochSeconds(0).toLocalDateTime(UTC)

public interface Interval {

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_floor). */
    public fun floor(input: LocalDateTime): LocalDateTime

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_ceil). */
    public fun ceil(input: LocalDateTime): LocalDateTime

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_round). */
    public fun round(input: LocalDateTime): LocalDateTime

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_offset). */
    public fun offset(input: LocalDateTime, steps: Int): LocalDateTime

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_range). */
    public fun range(start: LocalDateTime, stop: LocalDateTime, step: Int = 1): List<LocalDateTime>

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_filter). */
    public fun filter(test: (LocalDateTime) -> Boolean): Interval
}

public interface CountableInterval : Interval {

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_every). */
    public fun every(step: Int): Interval {
        require(step > 0) { "Step must be positive, but was $step." }
        if (step == 1) return this
        return when (this) {
            is Field -> filter { date -> field(date) % step == 0 }
            else -> filter { date -> count(UNIX_ZERO, date) % step == 0 }
        }
    }

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_count). */
    public fun count(start: LocalDateTime, stop: LocalDateTime): Int
}

public interface Field : CountableInterval {
    public fun field(input: LocalDateTime): Int
}
