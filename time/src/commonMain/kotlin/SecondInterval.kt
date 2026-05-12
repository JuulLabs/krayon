package com.juul.krayon.time

import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.seconds

public object SecondInterval : Interval, Interval.Count, Interval.Field {

    override fun floor(input: LocalDateTime): LocalDateTime =
        with(input) { LocalDateTime(year, month, day, hour, minute, second) }

    override fun offset(input: LocalDateTime, steps: Int): LocalDateTime =
        input + steps.seconds

    override fun count(start: LocalDateTime, stop: LocalDateTime): Int =
        (stop - start).inWholeSeconds.coerceToInt()

    override fun field(input: LocalDateTime): Int =
        input.second
}
