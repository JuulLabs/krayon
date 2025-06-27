@file:OptIn(ExperimentalTime::class)

package com.juul.krayon.scale

import com.juul.krayon.time.DayInterval
import com.juul.krayon.time.HourInterval
import com.juul.krayon.time.Interval
import com.juul.krayon.time.MillisecondInterval
import com.juul.krayon.time.MinuteInterval
import com.juul.krayon.time.MonthInterval
import com.juul.krayon.time.SecondInterval
import com.juul.krayon.time.WeekInterval
import com.juul.krayon.time.YearInterval
import com.juul.krayon.time.minus
import com.juul.krayon.time.plus
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toInstant
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

private data class NiceInterval(
    val interval: Interval.Count,
    val count: Int,
    val duration: Duration,
)

private val niceIntervals = listOf(
    NiceInterval(SecondInterval, 1, 1.seconds),
    NiceInterval(SecondInterval, 5, 5.seconds),
    NiceInterval(SecondInterval, 15, 15.seconds),
    NiceInterval(SecondInterval, 30, 30.seconds),
    NiceInterval(MinuteInterval, 1, 1.minutes),
    NiceInterval(MinuteInterval, 5, 5.minutes),
    NiceInterval(MinuteInterval, 15, 15.minutes),
    NiceInterval(MinuteInterval, 30, 30.minutes),
    NiceInterval(HourInterval, 1, 1.hours),
    NiceInterval(HourInterval, 3, 3.hours),
    NiceInterval(HourInterval, 6, 6.hours),
    NiceInterval(HourInterval, 12, 12.hours),
    NiceInterval(DayInterval, 1, 1.days),
    NiceInterval(HourInterval, 2, 2.days),
    // TODO: Figure out how to make this match the user's system.
    //       This behavior matches d3, though, so it can't be too bad.
    NiceInterval(WeekInterval.sunday, 1, 7.days),
    NiceInterval(MonthInterval, 1, 30.days),
    NiceInterval(MonthInterval, 3, 90.days),
    NiceInterval(YearInterval, 1, 365.days),
)

public fun ticks(start: LocalDateTime, stop: LocalDateTime, count: Int): List<LocalDateTime> {
    val reverse = stop < start
    val (min, max) = if (reverse) stop to start else start to stop
    val interval = tickInterval(min, max, count)
    val ticks = interval.range(min, max + 1.nanoseconds)
    return if (reverse) ticks.asReversed() else ticks
}

public fun tickInterval(start: LocalDateTime, stop: LocalDateTime, count: Int): Interval {
    val targetDuration = (stop - start).absoluteValue / count
    return if (targetDuration >= niceIntervals.last().duration) {
        YearInterval.every(max(1, tickStep(start.year.toDouble(), stop.year.toDouble(), count).roundToInt()))
    } else if (targetDuration < niceIntervals.first().duration) {
        val startMillis = start.toInstant(UTC).toEpochMilliseconds()
        val stopMillis = stop.toInstant(UTC).toEpochMilliseconds()
        MillisecondInterval.every(max(1, tickStep(startMillis.toDouble(), stopMillis.toDouble(), count).roundToInt()))
    } else {
        val (interval, step) = niceIntervals.minByOrNull { (_, _, duration) -> (duration - targetDuration).absoluteValue }
            ?: error("minByOrNull returned null for non-empty list.")
        interval.every(step)
    }
}
