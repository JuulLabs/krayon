package com.juul.krayon.time

import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

public object MillisecondInterval : Interval, Interval.Count, Interval.Field {

    override fun floor(input: LocalDateTime): LocalDateTime =
        with(input) { LocalDateTime(year, month, dayOfMonth, hour, minute, second, 1_000_000 * (nanosecond / 1_000_000)) }

    override fun offset(input: LocalDateTime, steps: Int): LocalDateTime =
        input + steps.milliseconds

    override fun count(start: LocalDateTime, stop: LocalDateTime): Int =
        (stop - start).inWholeMilliseconds.coerceToInt()

    override fun field(input: LocalDateTime): Int =
        input.nanosecond / 1_000_000
}
