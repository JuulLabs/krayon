package com.juul.krayon.interpolate

import kotlin.math.floor

/**
 * Chains a sequence of [values] into a single interpolator. Each adjacent pair is interpolated by
 * [interpolate], and the resulting segments are traversed evenly as `t` goes from `0` to `1`.
 */
public fun <T> piecewise(
    values: List<T>,
    interpolate: (T, T) -> Interpolator<T>,
): Interpolator<T> {
    require(values.isNotEmpty()) { "At least one value is required." }
    val n = values.size - 1
    val interpolators = List(n) { i -> interpolate(values[i], values[i + 1]) }
    return FunctionInterpolator { fraction ->
        if (n == 0) {
            values[0]
        } else {
            val scaled = fraction * n
            val i = floor(scaled).toInt().coerceIn(0, n - 1)
            interpolators[i].interpolate(scaled - i)
        }
    }
}
