package com.juul.krayon.collections

import kotlin.math.floor

/**
 * The [probability]-quantile (with `probability` in `[0, 1]`) using linear interpolation between
 * adjacent ranks. `NaN` values are ignored. Returns `null` when empty.
 */
public fun Iterable<Double>.quantile(probability: Double): Double? =
    filterNot { it.isNaN() }.sorted().quantileSorted(probability)

/**
 * The [probability]-quantile of a list already sorted in ascending order (and free of `NaN`).
 * Returns `null` when empty. Prefer this over [quantile] when the values are already sorted.
 */
public fun List<Double>.quantileSorted(probability: Double): Double? {
    if (isEmpty() || probability.isNaN()) return null
    if (probability <= 0 || size < 2) return first()
    if (probability >= 1) return last()
    val rank = (size - 1) * probability
    val lowerIndex = floor(rank).toInt()
    val lower = this[lowerIndex]
    val upper = this[lowerIndex + 1]
    return lower + (upper - lower) * (rank - lowerIndex)
}

/** The median value, equivalent to the `0.5` [quantile]. */
public fun Iterable<Double>.median(): Double? = quantile(0.5)
