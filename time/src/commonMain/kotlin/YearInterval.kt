package com.juul.krayon.time

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

public object YearInterval : Interval, Interval.Count, Interval.Field {

    override fun floor(input: LocalDateTime): LocalDateTime =
        with(input) { LocalDateTime(year, month = Month.JANUARY, day = 1, hour = 0, minute = 0) }

    override fun offset(input: LocalDateTime, steps: Int): LocalDateTime =
        input.date.plus(steps, DateTimeUnit.YEAR)
            .atTime(input.hour, input.minute, input.second, input.nanosecond)

    override fun count(start: LocalDateTime, stop: LocalDateTime): Int =
        stop.year - start.year

    override fun field(input: LocalDateTime): Int =
        input.year
}
