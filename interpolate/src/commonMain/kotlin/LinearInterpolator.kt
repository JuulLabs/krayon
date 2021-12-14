package com.juul.krayon.interpolate

import com.juul.krayon.color.Color
import com.juul.krayon.color.lerp
import com.juul.krayon.time.minus
import com.juul.krayon.time.plus
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.time.ExperimentalTime

public fun interpolator(start: Int, stop: Int): BidirectionalInterpolator<Int> = LinearIntInterpolator(start, stop)
public fun interpolator(start: Float, stop: Float): BidirectionalInterpolator<Float> = LinearFloatInterpolator(start, stop)
public fun interpolator(start: Double, stop: Double): BidirectionalInterpolator<Double> = LinearDoubleInterpolator(start, stop)
public fun interpolator(start: Instant, stop: Instant): BidirectionalInterpolator<Instant> = LinearInstantInterpolator(start, stop)
public fun interpolator(start: LocalDateTime, stop: LocalDateTime): BidirectionalInterpolator<LocalDateTime> = LinearLocalDateTimeInterpolator(start, stop)
public fun interpolator(start: Color, stop: Color): Interpolator<Color> = ArgbLinearColorInterpolator(start, stop)

private class LinearIntInterpolator(
    private val start: Int,
    stop: Int,
) : BidirectionalInterpolator<Int> {
    private val range = stop - start
    override fun interpolate(fraction: Float): Int = start + (range * fraction).toInt()
    override fun invert(value: Int): Float = (value - start).toFloat() / range
}

private class LinearFloatInterpolator(
    private val start: Float,
    stop: Float,
) : BidirectionalInterpolator<Float> {
    private val range = stop - start
    override fun interpolate(fraction: Float): Float = start + (range * fraction)
    override fun invert(value: Float): Float = (value - start) / range
}

private class LinearDoubleInterpolator(
    private val start: Double,
    stop: Double,
) : BidirectionalInterpolator<Double> {
    private val range = stop - start
    override fun interpolate(fraction: Float): Double = start + (range * fraction)
    override fun invert(value: Double): Float = ((value - start) / range).toFloat()
}

@OptIn(ExperimentalTime::class)
private class LinearInstantInterpolator(
    private val start: Instant,
    stop: Instant,
) : BidirectionalInterpolator<Instant> {
    private val range = stop - start
    override fun interpolate(fraction: Float): Instant = start + (range * fraction.toDouble())
    override fun invert(value: Instant): Float = ((value - start) / range).toFloat()
}

private class LinearLocalDateTimeInterpolator(
    private val start: LocalDateTime,
    stop: LocalDateTime,
) : BidirectionalInterpolator<LocalDateTime> {
    private val range = stop - start
    override fun interpolate(fraction: Float): LocalDateTime = start + (range * fraction.toDouble())
    override fun invert(value: LocalDateTime): Float = ((value - start) / range).toFloat()
}

private class ArgbLinearColorInterpolator(
    private val start: Color,
    private val stop: Color,
) : Interpolator<Color> {
    override fun interpolate(fraction: Float): Color = lerp(start, stop, fraction)
}
