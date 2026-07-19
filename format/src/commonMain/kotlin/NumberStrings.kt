package com.juul.krayon.format

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10

private class DoubleParts(val significand: Long, val binaryExponent: Int)

private fun decompose(absValue: Double): DoubleParts {
    val bits = absValue.toRawBits()
    val rawExponent = ((bits ushr 52) and 0x7FF).toInt()
    val mantissa = bits and 0x000FFFFFFFFFFFFFL
    return if (rawExponent == 0) {
        DoubleParts(mantissa, -1074)
    } else {
        DoubleParts(mantissa or 0x0010000000000000L, rawExponent - 1075)
    }
}

/** Returns `round(|value| * 10^scale)` as a non-negative integer, rounding halves away from zero. */
private fun roundToScaleBignum(value: Double, scale: Int): Bignum {
    val absValue = abs(value)
    if (absValue == 0.0) return Bignum.ZERO
    val parts = decompose(absValue)
    val powerOfTwo = parts.binaryExponent + scale
    val powerOfFive = scale
    val denominatorTwo = if (powerOfTwo < 0) -powerOfTwo else 0
    val denominatorFive = if (powerOfFive < 0) -powerOfFive else 0
    val commonDenominator = maxOf(denominatorTwo, denominatorFive)
    val exponentTwo = powerOfTwo + commonDenominator
    val exponentFive = powerOfFive + commonDenominator
    var numerator = Bignum.of(parts.significand).timesPow(2, exponentTwo).timesPow(5, exponentFive)
    if (commonDenominator == 0) return numerator
    val half = Bignum.of(5L).timesPow(10, commonDenominator - 1)
    numerator = numerator.plus(half)
    return numerator.divPow10(commonDenominator)
}

private fun compareToPowerOfTen(absValue: Double, exponent: Int): Int {
    val parts = decompose(absValue)
    val binaryExponent = parts.binaryExponent
    var lhs = Bignum.of(parts.significand)
    if (binaryExponent > 0) lhs = lhs.timesPow(2, binaryExponent)
    if (exponent < 0) lhs = lhs.timesPow(10, -exponent)
    var rhs = Bignum.of(1L)
    if (binaryExponent < 0) rhs = rhs.timesPow(2, -binaryExponent)
    if (exponent > 0) rhs = rhs.timesPow(10, exponent)
    return lhs.compareTo(rhs)
}

/** Exact `floor(log10(|value|))` for finite, non-zero input. */
private fun exactFloorLog10(absValue: Double): Int {
    var estimate = floor(log10(absValue)).toInt()
    while (compareToPowerOfTen(absValue, estimate) < 0) estimate--
    while (compareToPowerOfTen(absValue, estimate + 1) >= 0) estimate++
    return estimate
}

/** Correctly-rounded [precision] significant digits and the base-10 exponent of the first digit. */
private fun exactSignificantDigits(absValue: Double, precision: Int): Pair<String, Int> {
    val exponent = exactFloorLog10(absValue)
    val scale = precision - 1 - exponent
    val rounded = roundToScaleBignum(absValue, scale).toDecimalString()
    return if (rounded.length > precision) {
        rounded.substring(0, precision) to (exponent + 1)
    } else {
        rounded to exponent
    }
}

private fun trimTrailingZeros(digits: String): String {
    var end = digits.length
    while (end > 1 && digits[end - 1] == '0') end--
    return digits.substring(0, end)
}

/** Shortest round-tripping significant digits and base-10 exponent, mirroring `Number.prototype.toString`. */
private fun shortestDigits(absValue: Double): Pair<String, Int> {
    for (precision in 1..17) {
        val (digits, exponent) = exactSignificantDigits(absValue, precision)
        val rebuilt = buildString {
            append(digits[0])
            if (digits.length > 1) {
                append('.')
                append(digits.substring(1))
            }
            append('e')
            append(exponent)
        }
        if (rebuilt.toDouble() == absValue) {
            return trimTrailingZeros(digits) to exponent
        }
    }
    val (digits, exponent) = exactSignificantDigits(absValue, 17)
    return trimTrailingZeros(digits) to exponent
}

private fun significandDigits(absValue: Double, precision: Int): Pair<String, Int> {
    if (absValue == 0.0) return "0".repeat(precision) to 0
    return exactSignificantDigits(absValue, precision)
}

/** Mirror of d3's `formatDecimalParts`; `precision <= 0` uses the shortest representation. */
internal fun formatDecimalParts(value: Double, precision: Int): Pair<String, Int>? {
    val absValue = abs(value)
    if (!absValue.isFinite() || absValue == 0.0) return null
    return if (precision <= 0) shortestDigits(absValue) else exactSignificantDigits(absValue, precision)
}

/** Shortest-representation base-10 exponent, or `null` for zero and non-finite values. */
internal fun exponent10(value: Double): Int? {
    val absValue = abs(value)
    if (!absValue.isFinite() || absValue == 0.0) return null
    return shortestDigits(absValue).second
}

private fun exponentialForm(mantissaDigits: String, exponent: Int): String {
    val mantissa = if (mantissaDigits.length == 1) {
        mantissaDigits
    } else {
        mantissaDigits[0] + "." + mantissaDigits.substring(1)
    }
    return mantissa + "e" + (if (exponent < 0) "-" else "+") + abs(exponent)
}

internal fun formatFixed(absValue: Double, precision: Int): String {
    if (!absValue.isFinite()) return "Infinity"
    val digits = roundToScaleBignum(absValue, precision).toDecimalString()
    if (precision == 0) return digits
    val padded = digits.padStart(precision + 1, '0')
    val cut = padded.length - precision
    return padded.substring(0, cut) + "." + padded.substring(cut)
}

internal fun formatExponential(absValue: Double, precision: Int): String {
    if (!absValue.isFinite()) return "Infinity"
    val (digits, exponent) = significandDigits(absValue, precision + 1)
    val mantissa = if (precision == 0) digits else digits[0] + "." + digits.substring(1)
    return mantissa + "e" + (if (exponent < 0) "-" else "+") + abs(exponent)
}

internal fun formatPrecision(absValue: Double, precision: Int): String {
    if (!absValue.isFinite()) return "Infinity"
    val (digits, exponent) = significandDigits(absValue, precision)
    return when {
        exponent < -6 || exponent >= precision -> exponentialForm(digits, exponent)
        exponent >= 0 -> {
            val integerLength = exponent + 1
            if (integerLength >= precision) {
                digits
            } else {
                digits.substring(0, integerLength) + "." + digits.substring(integerLength)
            }
        }
        else -> "0." + "0".repeat(-exponent - 1) + digits
    }
}

internal fun formatDecimalInteger(absValue: Double): String {
    if (!absValue.isFinite()) return "Infinity"
    val rounded = floor(absValue + 0.5)
    if (rounded == 0.0) return "0"
    val (digits, exponent) = shortestDigits(rounded)
    return digits + "0".repeat(exponent - digits.length + 1)
}

internal fun formatRounded(absValue: Double, precision: Int): String {
    if (!absValue.isFinite()) return "Infinity"
    val (coefficient, exponent) = formatDecimalParts(absValue, precision) ?: return "0"
    return when {
        exponent < 0 -> "0." + "0".repeat(-exponent - 1) + coefficient
        coefficient.length > exponent + 1 ->
            coefficient.substring(0, exponent + 1) + "." + coefficient.substring(exponent + 1)
        else -> coefficient + "0".repeat(exponent - coefficient.length + 1)
    }
}

internal fun formatPrefixAuto(absValue: Double, precision: Int): Pair<String, Int?> {
    if (!absValue.isFinite()) return "Infinity" to null
    val (coefficient, exponent) = formatDecimalParts(absValue, precision)
        ?: return formatPrecision(absValue, precision) to null
    val prefixExponent = maxOf(-8, minOf(8, exponent.floorDiv(3))) * 3
    val shift = exponent - prefixExponent + 1
    val length = coefficient.length
    val text = when {
        shift == length -> coefficient
        shift > length -> coefficient + "0".repeat(shift - length)
        shift > 0 -> coefficient.substring(0, shift) + "." + coefficient.substring(shift)
        else -> {
            val inner = formatDecimalParts(absValue, maxOf(0, precision + shift - 1))?.first ?: "0"
            "0." + "0".repeat(-shift) + inner
        }
    }
    return text to prefixExponent
}

internal fun formatRadix(absValue: Double, radix: Int): String {
    if (!absValue.isFinite()) return "Infinity"
    return roundToScaleBignum(absValue, 0).toStringRadix(radix)
}

internal fun jsNumberToString(value: Double): String {
    if (value.isNaN()) return "NaN"
    if (value == 0.0) return "0"
    if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"
    val (digits, exponent) = shortestDigits(abs(value))
    val length = digits.length
    val body = when {
        exponent >= 21 || exponent <= -7 -> exponentialForm(digits, exponent)
        exponent >= 0 ->
            if (length <= exponent + 1) {
                digits + "0".repeat(exponent + 1 - length)
            } else {
                digits.substring(0, exponent + 1) + "." + digits.substring(exponent + 1)
            }
        else -> "0." + "0".repeat(-exponent - 1) + digits
    }
    return if (value < 0) "-$body" else body
}
