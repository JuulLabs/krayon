package com.juul.krayon.collections.internal

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt

private val e10 = sqrt(50.0)
private val e5 = sqrt(10.0)
private val e2 = sqrt(2.0)

internal data class TickSpec(val i1: Double, val i2: Double, val inc: Double)

internal fun tickSpec(start: Double, stop: Double, count: Int): TickSpec {
    val step = (stop - start) / max(0, count)
    val power = floor(log10(step))
    val error = step / 10.0.pow(power)
    val factor = when {
        error >= e10 -> 10.0
        error >= e5 -> 5.0
        error >= e2 -> 2.0
        else -> 1.0
    }
    var i1: Double
    var i2: Double
    var inc: Double
    if (power < 0) {
        inc = 10.0.pow(-power) / factor
        i1 = (start * inc).roundToLong().toDouble()
        i2 = (stop * inc).roundToLong().toDouble()
        if (i1 / inc < start) i1++
        if (i2 / inc > stop) i2--
        inc = -inc
    } else {
        inc = 10.0.pow(power) * factor
        i1 = (start / inc).roundToLong().toDouble()
        i2 = (stop / inc).roundToLong().toDouble()
        if (i1 * inc < start) i1++
        if (i2 * inc > stop) i2--
    }
    if (i2 < i1 && count in 1..1) return tickSpec(start, stop, count * 2)
    return TickSpec(i1, i2, inc)
}

internal fun ticks(start: Double, stop: Double, count: Int): List<Double> {
    if (count <= 0) return emptyList()
    if (start == stop) return listOf(start)
    val reverse = stop < start
    val (i1, i2, inc) = if (reverse) tickSpec(stop, start, count) else tickSpec(start, stop, count)
    if (i2 < i1) return emptyList()
    val n = (i2 - i1 + 1).toInt()
    val result = DoubleArray(n)
    if (reverse) {
        if (inc < 0) {
            for (i in 0 until n) result[i] = (i2 - i) / -inc
        } else {
            for (i in 0 until n) result[i] = (i2 - i) * inc
        }
    } else {
        if (inc < 0) {
            for (i in 0 until n) result[i] = (i1 + i) / -inc
        } else {
            for (i in 0 until n) result[i] = (i1 + i) * inc
        }
    }
    return result.asList()
}

internal fun tickIncrement(start: Double, stop: Double, count: Int): Double =
    tickSpec(start, stop, count).inc

internal fun nice(start: Double, stop: Double, count: Int): Pair<Double, Double> {
    var lower = start
    var upper = stop
    var previousStep: Double? = null
    while (true) {
        val step = tickIncrement(lower, upper, count)
        if (step == previousStep || step == 0.0 || !step.isFinite()) {
            return lower to upper
        } else if (step > 0) {
            lower = floor(lower / step) * step
            upper = ceil(upper / step) * step
        } else {
            lower = ceil(lower * step) / step
            upper = floor(upper * step) / step
        }
        previousStep = step
    }
}
