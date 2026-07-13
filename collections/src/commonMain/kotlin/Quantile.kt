package com.juul.krayon.collections

import kotlin.math.floor

/**
 * Computes the [p]-quantile (with `p` in `[0, 1]`) of the numeric values produced by [selector],
 * using linear interpolation. See equivalent [d3 function](https://github.com/d3/d3-array#quantile).
 */
public inline fun <T> Iterable<T>.quantile(p: Double, selector: (T) -> Double?): Double? {
    val numbers = ArrayList<Double>()
    for (element in this) {
        val value = selector(element) ?: continue
        if (!value.isNaN()) numbers.add(value)
    }
    numbers.sort()
    return numbers.quantileSorted(p) { it }
}

/**
 * Computes the [p]-quantile of the values produced by [selector], assuming the receiver is already
 * sorted in ascending order by those values. See equivalent
 * [d3 function](https://github.com/d3/d3-array#quantileSorted).
 */
public inline fun <T> List<T>.quantileSorted(p: Double, selector: (T) -> Double): Double? {
    val n = size
    if (n == 0 || p.isNaN()) return null
    if (p <= 0 || n < 2) return selector(this[0])
    if (p >= 1) return selector(this[n - 1])
    val i = (n - 1) * p
    val i0 = floor(i).toInt()
    val value0 = selector(this[i0])
    val value1 = selector(this[i0 + 1])
    return value0 + (value1 - value0) * (i - i0)
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#median). */
public inline fun <T> Iterable<T>.median(selector: (T) -> Double?): Double? =
    quantile(0.5, selector)
