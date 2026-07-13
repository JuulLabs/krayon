package com.juul.krayon.interpolate

/**
 * Interpolates element-wise between [start] and [stop]. The result has the length of [stop];
 * indices beyond the length of [start] are taken from [stop] directly, mirroring d3's
 * `interpolateArray`.
 */
public fun <T> interpolateList(
    start: List<T>,
    stop: List<T>,
    interpolate: (T, T) -> Interpolator<T>,
): Interpolator<List<T>> {
    val nb = stop.size
    val na = minOf(start.size, nb)
    val interpolators = List(na) { i -> interpolate(start[i], stop[i]) }
    return FunctionInterpolator { t ->
        List(nb) { i -> if (i < na) interpolators[i].interpolate(t) else stop[i] }
    }
}
