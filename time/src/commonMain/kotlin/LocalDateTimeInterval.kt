package com.juul.krayon.time

import kotlinx.datetime.LocalDateTime
import kotlin.math.abs
import kotlin.time.Duration.Companion.nanoseconds

public abstract class LocalDateTimeInterval : Interval {

    override fun ceil(input: LocalDateTime): LocalDateTime =
        if (input == floor(input)) input else floor(offset(input, 1))

    override fun round(input: LocalDateTime): LocalDateTime {
        val lower = floor(input)
        val upper = ceil(input)
        return if (input - lower < input - upper) lower else upper
    }

    override fun range(start: LocalDateTime, stop: LocalDateTime, step: Int): List<LocalDateTime> {
        require(step > 0) { "Step must be positive, but was $step." }
        var next = ceil(start)
        if (stop <= next) return emptyList()
        return buildList {
            do {
                val previous = next
                this += previous
                next = floor(offset(next, step))
            } while (previous < next && next < stop)
        }
    }

    override fun filter(test: (LocalDateTime) -> Boolean): LocalDateTimeInterval {
        val parent = this
        return object : LocalDateTimeInterval() {
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
}
