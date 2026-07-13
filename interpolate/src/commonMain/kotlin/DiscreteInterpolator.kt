package com.juul.krayon.interpolate

import kotlin.math.floor

/**
 * Returns an interpolator that maps `t` in `[0, 1]` to one of the values in [range] by flooring
 * `t * range.size`, clamping to valid indices.
 */
public fun <T> interpolateDiscrete(range: List<T>): Interpolator<T> {
    require(range.isNotEmpty()) { "At least one value is required." }
    val n = range.size
    return FunctionInterpolator { t -> range[floor(t * n).toInt().coerceIn(0, n - 1)] }
}
