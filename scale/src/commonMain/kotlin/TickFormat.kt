package com.juul.krayon.scale

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.roundToLong

/**
 * Returns a formatter suitable for labelling ticks spanning [start] to [stop] with approximately [count] steps.
 *
 * The formatter rounds each value to the number of decimal places implied by the tick step (see [precisionFixed]).
 * This is a lightweight analogue of [d3's tickFormat](https://github.com/d3/d3-scale#tickFormat) without the full
 * d3-format specifier language.
 */
public fun tickFormat(start: Float, stop: Float, count: Int): (Float) -> String {
    val precision = precisionFixed(tickStep(start, stop, count).toDouble())
    return { value -> formatFixed(value.toDouble(), precision) }
}

/** [Double] equivalent of [tickFormat]. */
public fun tickFormat(start: Double, stop: Double, count: Int): (Double) -> String {
    val precision = precisionFixed(tickStep(start, stop, count))
    return { value -> formatFixed(value, precision) }
}

/** The number of decimal places needed to represent a fixed-point value spaced by [step]. */
internal fun precisionFixed(step: Double): Int {
    if (step == 0.0 || step.isNaN() || step.isInfinite()) return 0
    return maxOf(0, -floor(log10(abs(step))).toInt())
}

/** Formats [value] with exactly [decimals] digits after the decimal point (no locale, no grouping). */
internal fun formatFixed(value: Double, decimals: Int): String {
    if (value.isNaN()) return "NaN"
    if (value.isInfinite()) return if (value > 0.0) "Infinity" else "-Infinity"

    val negative = value < 0.0
    val magnitude = abs(value)
    val factor = tenToThe(decimals)
    val scaled = (magnitude * factor).roundToLong()

    val text = if (decimals <= 0) {
        scaled.toString()
    } else {
        val digits = scaled.toString().padStart(decimals + 1, '0')
        val pointIndex = digits.length - decimals
        digits.substring(0, pointIndex) + "." + digits.substring(pointIndex)
    }

    return if (negative && scaled != 0L) "-$text" else text
}

/** Default label for a numeric value, dropping a redundant trailing `.0`. */
internal fun defaultNumberFormat(value: Float): String {
    val text = value.toString()
    return if (text.endsWith(".0")) text.dropLast(2) else text
}

private fun tenToThe(power: Int): Double {
    var result = 1.0
    repeat(abs(power)) { result *= 10.0 }
    return if (power >= 0) result else 1.0 / result
}
