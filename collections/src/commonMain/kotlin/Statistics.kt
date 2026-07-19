package com.juul.krayon.collections

import kotlin.math.sqrt

/**
 * The unbiased sample variance (dividing by `n - 1`), computed with Welford's online algorithm.
 * `NaN` values are ignored. Returns `null` when fewer than two values are present.
 */
public fun Iterable<Double>.variance(): Double? {
    var count = 0
    var mean = 0.0
    var sumOfSquaredDeltas = 0.0
    for (value in this) {
        if (value.isNaN()) continue
        val delta = value - mean
        mean += delta / ++count
        sumOfSquaredDeltas += delta * (value - mean)
    }
    return if (count > 1) sumOfSquaredDeltas / (count - 1) else null
}

/** The sample standard deviation, or `null` when [variance] is undefined. */
public fun Iterable<Double>.standardDeviation(): Double? = variance()?.let(::sqrt)
