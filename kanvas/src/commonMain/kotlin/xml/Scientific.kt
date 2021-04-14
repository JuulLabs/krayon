package com.juul.krayon.kanvas.xml

import kotlin.math.log
import kotlin.math.nextUp
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Returns a string representing this number in scientific e notation.
 *
 * [maxPrecision] is maximum number of digits to include. This is not the same as significant figures, as trailing zeroes are omitted.
 *
 * Note that the number is first cast to a [Double], so large values of [Long] may be rounded before being converted.
 */
internal fun Number.scientificNotation(maxPrecision: Int): String = this.toDouble().scientificNotation(maxPrecision)

/**
 * Returns a string representing this number in scientific e notation.
 *
 * [maxPrecision] is maximum number of digits to include. This is not the same as significant figures, as trailing zeroes are omitted.
 */
internal fun Double.scientificNotation(maxPrecision: Int): String = when {
    this < 0 -> "-" + (-this).scientificNotation(maxPrecision)
    this == 0.0 -> "0"
    else -> this.asComponents().format(maxPrecision)
}

private data class Components(
    val integral: Int,
    val fractional: Double,
    val magnitude: Int
) {
    init {
        require(integral in 0..9)
        require(fractional >= 0f && fractional < 1f)
    }
}

private fun Double.asComponents(): Components {
    val integral = toLong()
    val fractional = this - integral
    return when {
        toLong() == 0L -> {
            val magnitude = (1 + log(fractional, 0.1)).toInt()
            val scaled = (this * 10.0.pow(magnitude)).nextUp()
            Components(scaled.toInt(), scaled - scaled.toInt(), -magnitude)
        }
        toLong() >= 10L -> {
            val magnitude = log(integral.toDouble(), 10.0).toInt()
            val scaled = (this / 10.0.pow(magnitude)).nextUp()
            Components(scaled.toInt(), scaled - scaled.toInt(), magnitude)
        }
        else -> Components(integral.toInt(), fractional, 0)
    }
}

private fun Components.format(maxPrecision: Int): String {
    require(maxPrecision >= 1)
    return buildString {
        append(integral)
        if (fractional != 0.0) {
            val fractionalString = fractionalString(maxDigits = maxPrecision - 1)
            if (fractionalString.isNotEmpty()) {
                append('.')
                append(fractionalString)
            }
        }
        if (magnitude != 0) {
            append('e')
            append(magnitude)
        }
    }
}

private fun Components.fractionalString(maxDigits: Int): String = buildString {
    var remaining = fractional
    var remainingFigures = maxDigits
    while (remaining > 0.0 && remainingFigures > 0) {
        val next = remaining * 10
        val digit = when (remainingFigures) {
            1 -> next.roundToInt()
            else -> next.toInt()
        }
        append(digit)
        remaining = next - digit
        remainingFigures -= 1
    }
}.trimEnd('0')
