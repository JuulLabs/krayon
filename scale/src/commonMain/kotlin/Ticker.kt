package com.juul.krayon.scale

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import com.juul.krayon.scale.ticks as rawTicks

public fun interface Ticker<T> {
    public fun ticks(start: T, stop: T, count: Int): List<T>
}

public object FloatTicker : Ticker<Float> {
    override fun ticks(start: Float, stop: Float, count: Int): List<Float> =
        rawTicks(start, stop, count)
}

public object DoubleTicker : Ticker<Double> {
    override fun ticks(start: Double, stop: Double, count: Int): List<Double> =
        rawTicks(start, stop, count)
}

public object LocalDateTimeTicker : Ticker<LocalDateTime> {
    override fun ticks(start: LocalDateTime, stop: LocalDateTime, count: Int): List<LocalDateTime> =
        rawTicks(start, stop, count)
}

public object InstantTicker : Ticker<Instant> {
    override fun ticks(start: Instant, stop: Instant, count: Int): List<Instant> =
        rawTicks(start.toLocalDateTime(UTC), stop.toLocalDateTime(UTC), count)
            .map { it.toInstant(UTC) }
}
