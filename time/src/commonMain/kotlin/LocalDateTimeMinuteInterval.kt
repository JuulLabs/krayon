package com.juul.krayon.time

import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.minutes

public object LocalDateTimeMinuteInterval : EveryableLocalDateTimeInterval() {

    override fun floor(input: LocalDateTime): LocalDateTime =
        with(input) { LocalDateTime(year, month, dayOfMonth, hour, minute) }

    override fun offset(input: LocalDateTime, steps: Int): LocalDateTime =
        input + steps.minutes

    override fun count(start: LocalDateTime, stop: LocalDateTime): Int =
        (stop - start).inWholeMinutes.coerceToInt()

    override fun field(input: LocalDateTime): Int =
        input.minute
}
