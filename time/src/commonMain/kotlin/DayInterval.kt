package com.juul.krayon.time

import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.days

public object DayInterval : Interval, Interval.Count, Interval.Field {

    override fun floor(input: LocalDateTime): LocalDateTime =
        with(input) { LocalDateTime(year, month, dayOfMonth, hour = 0, minute = 0) }

    override fun offset(input: LocalDateTime, steps: Int): LocalDateTime =
        input + steps.days

    override fun count(start: LocalDateTime, stop: LocalDateTime): Int =
        (stop - start).inWholeDays.coerceToInt()

    override fun field(input: LocalDateTime): Int =
        input.dayOfMonth
}
