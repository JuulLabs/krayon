package com.juul.krayon.interpolate

/**
 * Samples [interpolator] at [n] uniformly-spaced points in `[0, 1]`, returning the resulting
 * values. Requires [n] to be at least `2`.
 */
public fun <T> quantize(n: Int, interpolator: Interpolator<T>): List<T> {
    require(n >= 2) { "At least two samples are required." }
    return List(n) { i -> interpolator.interpolate(i.toFloat() / (n - 1)) }
}
