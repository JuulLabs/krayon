package com.juul.krayon.scale

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

// A friendly reminder to thank your local math-magician, because I don't know what's happening in most of this file.

private val sqrt50 = sqrt(50.0)
private val sqrt10 = sqrt(10.0)
private val sqrt2 = sqrt(2.0)
private val ln10 = ln(10.0)

/** See equivalent [d3 function](https://github.com/d3/d3-array#tick). */
public fun ticks(start: Float, stop: Float, count: Int): List<Float> =
    ticks(start.toDouble(), stop.toDouble(), count).map { it.toFloat() }

/** See equivalent [d3 function](https://github.com/d3/d3-array#tick). */
public fun ticks(start: Double, stop: Double, count: Int): List<Double> {
    require(count > 0) { "Count must be positive, but was $count." }
    if (start == stop) return listOf(start)
    val reverse = stop < start
    val (min, max) = if (reverse) stop to start else start to stop
    val step = tickIncrement(min, max, count).toDouble()

    val ticks = if (step > 0) {
        var r0 = (min / step).roundToInt()
        if (r0 * step < start) r0 += 1
        var r1 = (max / step).roundToInt()
        if (r1 * step > stop) r1 -= 1

        val capacity = r1 - r0 + 1
        (0 until capacity).map { i -> (r0 + i) * step }
    } else {
        val negativeInverseStep = -step
        var r0 = (min * negativeInverseStep).roundToInt()
        if (r0 / negativeInverseStep < start) r0 += 1
        var r1 = (max * negativeInverseStep).roundToInt()
        if (r1 / negativeInverseStep > stop) r1 -= 1

        val capacity = r1 - r0 + 1
        (0 until capacity).map { i -> (r0 + i) / negativeInverseStep }
    }

    return if (reverse) ticks.asReversed() else ticks
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#tickIncrement). */
public fun tickIncrement(start: Float, stop: Float, count: Int): Int =
    tickIncrement(start.toDouble(), stop.toDouble(), count)

/** See equivalent [d3 function](https://github.com/d3/d3-array#tickIncrement). */
public fun tickIncrement(start: Double, stop: Double, count: Int): Int {
    require(count > 0) { "Count must be positive, but was $count." }
    if (start == stop) return 0
    require(start < stop) { "Start must be less than or equal to stop, but values were $start and $stop." }
    val step = (stop - start) / count
    val power = floor(log10(step))
    val error = step / (10.0).pow(power)
    return if (power >= 0) {
        errorScaleOf(error) * (10.0).pow(power)
    } else {
        -((10.0).pow(-power) / errorScaleOf(error))
    }.roundToInt()
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#tickStep). */
public fun tickStep(start: Float, stop: Float, count: Int): Float =
    tickStep(start.toDouble(), stop.toDouble(), count).toFloat()

/** See equivalent [d3 function](https://github.com/d3/d3-array#tickStep). */
public fun tickStep(start: Double, stop: Double, count: Int): Double {
    require(count > 0) { "Count must be positive, but was $count." }
    if (start == stop) return 0.0
    val preciseStep = abs(stop - start) / count
    val roundedStep = (10.0).pow(floor(log10(preciseStep)))
    val error = preciseStep / roundedStep
    val finalStep = errorScaleOf(error) * roundedStep
    return if (start < stop) finalStep else -finalStep
}

private fun errorScaleOf(error: Double): Int = when {
    error >= sqrt50 -> 10
    error >= sqrt10 -> 5
    error >= sqrt2 -> 2
    else -> 1
}
