package com.juul.krayon.time

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Duration.Companion.nanoseconds

private val UNIX_ZERO = Instant.fromEpochSeconds(0).toLocalDateTime(UTC)

public interface Interval {

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_floor). */
    public fun floor(input: LocalDateTime): LocalDateTime

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_ceil). */
    public fun ceil(input: LocalDateTime): LocalDateTime =
        if (input == floor(input)) input else floor(offset(input, 1))

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_round). */
    public fun round(input: LocalDateTime): LocalDateTime {
        val lower = floor(input)
        val upper = ceil(input)
        return if (input - lower < input - upper) lower else upper
    }

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_offset). */
    public fun offset(input: LocalDateTime, steps: Int): LocalDateTime

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_range). */
    public fun range(start: LocalDateTime, stop: LocalDateTime, step: Int = 1): List<LocalDateTime> {
        require(step > 0) { "Step must be positive, but was $step." }
        var next = floor(start)
        if (stop <= next) return emptyList()
        return buildList {
            do {
                val previous = next
                this += previous
                next = floor(offset(next, step))
            } while (previous < next && next < stop)
        }
    }

    /** See equivalent [d3 function](https://github.com/d3/d3-time#interval_filter). */
    public fun filter(test: (LocalDateTime) -> Boolean): Interval {
        val parent = this
        return object : Interval {
            override fun floor(input: LocalDateTime): LocalDateTime {
                var date = parent.floor(input)
                while (!test(date)) {
                    date = parent.floor(date - 1.nanoseconds)
                }
                return date
            }

            override fun offset(input: LocalDateTime, steps: Int): LocalDateTime {
                val direction = if (steps < 0) -1 else 1
                var date = input
                for (step in 0 until abs(steps)) {
                    do {
                        date = parent.offset(date, direction)
                    } while (!test(date))
                }
                return date
            }
        }
    }

    public interface Count : Interval {

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

    public interface Field : Interval {

        /** Not exposed by D3's interface, and is instead an implementation detail of [newInterval](https://github.com/d3/d3-time/blob/main/src/interval.js). */
        public fun field(input: LocalDateTime): Int
    }
}
