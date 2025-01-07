package com.juul.krayon.interpolate

import com.juul.krayon.color.Color
import com.juul.krayon.color.lerp
import com.juul.krayon.time.minus
import com.juul.krayon.time.plus
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus
import kotlin.math.roundToInt

internal class LinearIntInterpolator(
    private val start: Int,
    stop: Int,
) : BidirectionalInterpolator<Int> {
    private val range = stop - start

    override fun interpolate(fraction: Float): Int = start + (range * fraction).toInt()

    override fun invert(value: Int): Float = (value - start).toFloat() / range
}

internal class LinearFloatInterpolator(
    private val start: Float,
    stop: Float,
) : BidirectionalInterpolator<Float> {
    private val range = stop - start

    override fun interpolate(fraction: Float): Float = start + (range * fraction)

    override fun invert(value: Float): Float = (value - start) / range
}

internal class LinearDoubleInterpolator(
    private val start: Double,
    stop: Double,
) : BidirectionalInterpolator<Double> {
    private val range = stop - start

    override fun interpolate(fraction: Float): Double = start + (range * fraction)

    override fun invert(value: Double): Float = ((value - start) / range).toFloat()
}

internal class LinearInstantInterpolator(
    private val start: Instant,
    stop: Instant,
) : BidirectionalInterpolator<Instant> {
    private val range = stop - start

    override fun interpolate(fraction: Float): Instant = start + (range * fraction.toDouble())

    override fun invert(value: Instant): Float = ((value - start) / range).toFloat()
}

internal class LinearLocalDateInterpolator(
    private val start: LocalDate,
    stop: LocalDate,
) : BidirectionalInterpolator<LocalDate> {

    private val range = stop.toEpochDays() - start.toEpochDays()

    override fun interpolate(fraction: Float): LocalDate =
        start + DatePeriod(days = (range * fraction).roundToInt())

    override fun invert(value: LocalDate): Float =
        (value.toEpochDays() - start.toEpochDays()).toFloat() / range
}

internal class LinearLocalDateTimeInterpolator(
    private val start: LocalDateTime,
    stop: LocalDateTime,
) : BidirectionalInterpolator<LocalDateTime> {
    private val range = stop - start

    override fun interpolate(fraction: Float): LocalDateTime = start + (range * fraction.toDouble())

    override fun invert(value: LocalDateTime): Float = ((value - start) / range).toFloat()
}

internal class ArgbLinearColorInterpolator(
    private val start: Color,
    private val stop: Color,
) : Interpolator<Color> {
    override fun interpolate(fraction: Float): Color = lerp(start, stop, fraction)
}
