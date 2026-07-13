package com.juul.krayon.format

import kotlin.math.abs

/** The default `en-US` locale definition. See [d3](https://github.com/d3/d3-format/blob/main/locale/en-US.json). */
public val enUS: FormatLocaleDefinition = FormatLocaleDefinition(
    decimal = ".",
    thousands = ",",
    grouping = listOf(3),
    currencyPrefix = "$",
    currencySuffix = "",
)

private val defaultLocale = FormatLocale(enUS)

/** Builds a formatter from a [specifier] string using the default [enUS] locale. */
public fun format(specifier: String): NumberFormat = defaultLocale.format(specifier)

/** Builds a formatter from a parsed [specifier] using the default [enUS] locale. */
public fun format(specifier: FormatSpecifier): NumberFormat = defaultLocale.format(specifier)

/** Builds an SI-prefix formatter for [value] using the default [enUS] locale. */
public fun formatPrefix(specifier: String, value: Double): NumberFormat = defaultLocale.formatPrefix(specifier, value)

/**
 * Returns the suggested decimal precision for fixed-point notation given a [step].
 *
 * See the equivalent [d3 function](https://github.com/d3/d3-format#precisionFixed).
 */
public fun precisionFixed(step: Double): Int {
    val exponent = exponent10(step) ?: throw IllegalArgumentException("step must be finite and non-zero, but was $step")
    return maxOf(0, -exponent)
}

/**
 * Returns the suggested decimal precision for rounding to significant digits over a [step] and [max] range.
 *
 * See the equivalent [d3 function](https://github.com/d3/d3-format#precisionRound).
 */
public fun precisionRound(step: Double, max: Double): Int {
    val absStep = abs(step)
    val stepExponent = exponent10(absStep)
        ?: throw IllegalArgumentException("step must be finite and non-zero, but was $step")
    val difference = abs(max) - absStep
    val differenceExponent = exponent10(difference)
        ?: throw IllegalArgumentException("abs(max) - abs(step) must be finite and non-zero, but was $difference")
    return maxOf(0, differenceExponent - stepExponent) + 1
}

/**
 * Returns the suggested decimal precision for SI-prefix notation given a [step] and reference [value].
 *
 * See the equivalent [d3 function](https://github.com/d3/d3-format#precisionPrefix).
 */
public fun precisionPrefix(step: Double, value: Double): Int {
    val valueExponent = exponent10(value)
        ?: throw IllegalArgumentException("value must be finite and non-zero, but was $value")
    val stepExponent = exponent10(step)
        ?: throw IllegalArgumentException("step must be finite and non-zero, but was $step")
    return maxOf(0, maxOf(-8, minOf(8, valueExponent.floorDiv(3))) * 3 - stepExponent)
}
