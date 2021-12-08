package com.juul.krayon.time

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlin.time.Duration.Companion.days

public class WeekInterval private constructor(
    private val dayOfWeek: DayOfWeek
) : Interval, Interval.Count {

    // TODO: Optimize this via math
    override fun floor(input: LocalDateTime): LocalDateTime =
        generateSequence(input.date) { it.minus(1, DateTimeUnit.DAY) }
            .first { it.dayOfWeek == dayOfWeek }
            .atTime(0, 0)

    override fun offset(input: LocalDateTime, steps: Int): LocalDateTime =
        input + (steps * 7).days

    override fun count(start: LocalDateTime, stop: LocalDateTime): Int =
        ((stop - start).inWholeDays / 7).coerceToInt()

    public companion object {
        public val monday: WeekInterval = WeekInterval(DayOfWeek.MONDAY)
        public val tuesday: WeekInterval = WeekInterval(DayOfWeek.TUESDAY)
        public val wednesday: WeekInterval = WeekInterval(DayOfWeek.WEDNESDAY)
        public val thursday: WeekInterval = WeekInterval(DayOfWeek.THURSDAY)
        public val friday: WeekInterval = WeekInterval(DayOfWeek.FRIDAY)
        public val saturday: WeekInterval = WeekInterval(DayOfWeek.SATURDAY)
        public val sunday: WeekInterval = WeekInterval(DayOfWeek.SUNDAY)
    }
}
