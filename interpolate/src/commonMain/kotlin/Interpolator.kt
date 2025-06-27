@file:OptIn(ExperimentalTime::class)

package com.juul.krayon.interpolate

import com.juul.krayon.color.Color
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

public interface Interpolator<T> {
    public fun interpolate(fraction: Float): T
}

public fun interpolator(
    start: Int,
    stop: Int,
): BidirectionalInterpolator<Int> = LinearIntInterpolator(start, stop)

public fun interpolator(
    start: Float,
    stop: Float,
): BidirectionalInterpolator<Float> = LinearFloatInterpolator(start, stop)

public fun interpolator(
    start: Double,
    stop: Double,
): BidirectionalInterpolator<Double> = LinearDoubleInterpolator(start, stop)

public fun interpolator(
    start: Instant,
    stop: Instant,
): BidirectionalInterpolator<Instant> = LinearInstantInterpolator(start, stop)

public fun interpolator(
    start: LocalDate,
    stop: LocalDate,
): BidirectionalInterpolator<LocalDate> = LinearLocalDateInterpolator(start, stop)

public fun interpolator(
    start: LocalDateTime,
    stop: LocalDateTime,
): BidirectionalInterpolator<LocalDateTime> = LinearLocalDateTimeInterpolator(start, stop)

public fun interpolator(
    start: Color,
    stop: Color,
): Interpolator<Color> = ArgbLinearColorInterpolator(start, stop)
