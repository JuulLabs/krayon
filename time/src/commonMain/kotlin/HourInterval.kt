package com.juul.krayon.time

import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.hours

public object HourInterval : Interval, Interval.Count, Interval.Field {

    override fun floor(input: LocalDateTime): LocalDateTime =
        with(input) { LocalDateTime(year, month, dayOfMonth, hour, minute = 0) }

    override fun offset(input: LocalDateTime, steps: Int): LocalDateTime =
        input + steps.hours

    override fun count(start: LocalDateTime, stop: LocalDateTime): Int =
        (stop - start).inWholeHours.coerceToInt()

    override fun field(input: LocalDateTime): Int =
        input.hour
}
