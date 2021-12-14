package com.juul.krayon.time

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

public object MonthInterval : Interval, Interval.Count, Interval.Field {

    override fun floor(input: LocalDateTime): LocalDateTime =
        with(input) { LocalDateTime(year, month, dayOfMonth = 1, hour = 0, minute = 0) }

    override fun offset(input: LocalDateTime, steps: Int): LocalDateTime =
        input.date.plus(steps, DateTimeUnit.MONTH)
            .atTime(input.hour, input.minute, input.second, input.nanosecond)

    override fun count(start: LocalDateTime, stop: LocalDateTime): Int =
        (stop.monthNumber - start.monthNumber) + (12 * (stop.year - start.year))

    // Offset by 1 to match JS date behavior (0 to 11)
    override fun field(input: LocalDateTime): Int =
        input.monthNumber - 1
}
