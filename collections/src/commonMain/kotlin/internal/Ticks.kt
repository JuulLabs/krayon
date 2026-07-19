package com.juul.krayon.collections.internal

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt

// Ported from d3-array's ticks.js to keep histogram bin boundaries identical to d3. Kept internal
// because the public `ticks`/`tickIncrement`/`nice` helpers already live in the `scale` module.

private val sqrt50 = sqrt(50.0)
private val sqrt10 = sqrt(10.0)
private val sqrt2 = sqrt(2.0)

private data class TickSpec(val firstStep: Double, val lastStep: Double, val increment: Double)

private fun tickSpec(start: Double, stop: Double, count: Int): TickSpec {
    val rawStep = (stop - start) / max(0, count)
    val power = floor(log10(rawStep))
    val error = rawStep / 10.0.pow(power)
    val factor = when {
        error >= sqrt50 -> 10.0
        error >= sqrt10 -> 5.0
        error >= sqrt2 -> 2.0
        else -> 1.0
    }
    var firstStep: Double
    var lastStep: Double
    var increment: Double
    if (power < 0) {
        increment = 10.0.pow(-power) / factor
        firstStep = (start * increment).roundToLong().toDouble()
        lastStep = (stop * increment).roundToLong().toDouble()
        if (firstStep / increment < start) firstStep++
        if (lastStep / increment > stop) lastStep--
        increment = -increment
    } else {
        increment = 10.0.pow(power) * factor
        firstStep = (start / increment).roundToLong().toDouble()
        lastStep = (stop / increment).roundToLong().toDouble()
        if (firstStep * increment < start) firstStep++
        if (lastStep * increment > stop) lastStep--
    }
    if (lastStep < firstStep && count in 1..1) return tickSpec(start, stop, count * 2)
    return TickSpec(firstStep, lastStep, increment)
}

internal fun ticks(start: Double, stop: Double, count: Int): List<Double> {
    if (count <= 0) return emptyList()
    if (start == stop) return listOf(start)
    val reverse = stop < start
    val (firstStep, lastStep, increment) = if (reverse) tickSpec(stop, start, count) else tickSpec(start, stop, count)
    if (lastStep < firstStep) return emptyList()
    val size = (lastStep - firstStep + 1).toInt()
    return List(size) { i ->
        val step = if (reverse) lastStep - i else firstStep + i
        if (increment < 0) step / -increment else step * increment
    }
}

internal fun tickIncrement(start: Double, stop: Double, count: Int): Double =
    tickSpec(start, stop, count).increment

internal fun nice(start: Double, stop: Double, count: Int): Pair<Double, Double> {
    var lower = start
    var upper = stop
    var previousIncrement: Double? = null
    while (true) {
        val increment = tickIncrement(lower, upper, count)
        if (increment == previousIncrement || increment == 0.0 || !increment.isFinite()) {
            return lower to upper
        } else if (increment > 0) {
            lower = floor(lower / increment) * increment
            upper = ceil(upper / increment) * increment
        } else {
            lower = ceil(lower * increment) / increment
            upper = floor(upper * increment) / increment
        }
        previousIncrement = increment
    }
}
