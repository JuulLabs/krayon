package com.juul.krayon.scale

import com.juul.krayon.interpolate.Interpolator
import com.juul.krayon.interpolate.Inverter
import com.juul.krayon.interpolate.interpolator

/**
 * Shared piecewise machinery for nonlinear continuous scales (log, pow, symlog). The [transform] is applied to domain
 * values before linear interpolation, and [untransform] reverses it for [invert]. Values outside the domain are
 * linearly extrapolated unless [clamp] is set.
 */
internal class TransformedScale<R>(
    private val domain: List<Float>,
    private val range: List<R>,
    private val transform: (Float) -> Float,
    private val untransform: (Float) -> Float,
    private val clamp: Boolean,
    private val getInterpolator: (R, R) -> Interpolator<R>,
) {

    private inner class State {
        init {
            require(domain.size >= 2) { "Domain must consist of at least 2 values, but was ${domain.size}." }
            require(range.size >= 2) { "Range must consist of at least 2 values, but was ${range.size}." }
            require(domain.size == range.size) {
                "Domain and range must have the same number of values, but domain was ${domain.size} and range was ${range.size}."
            }
            require(domain.isStrictlyAscending() || domain.isStrictlyDescending()) {
                "Domain must be strictly ascending or strictly descending."
            }
        }

        val reversed = domain[1] < domain[0]
        val sortedDomain = if (!reversed) domain else domain.asReversed()
        val sortedRange = if (!reversed) range else range.asReversed()
        val lo = sortedDomain.first()
        val hi = sortedDomain.last()
        val transformedDomain = sortedDomain.map(transform)
        val transformedMaps = transformedDomain.zipWithNext(::interpolator)
        val interpolators = sortedRange.zipWithNext(getInterpolator)
    }

    private val state by lazy { State() }

    fun scale(input: Float): R = with(state) {
        val coerced = if (clamp) input.coerceIn(lo, hi) else input
        val transformed = transform(coerced)
        val i = segmentIndex(transformedDomain, transformed)
        interpolators[i].interpolate(transformedMaps[i].invert(transformed))
    }

    @Suppress("UNCHECKED_CAST")
    fun invert(value: R): Float = with(state) {
        val count = interpolators.size
        var i = 0
        while (i < count) {
            val inverter = interpolators[i] as? Inverter<R>
                ?: throw UnsupportedOperationException("Cannot invert a scale whose range is not invertible.")
            val fraction = inverter.invert(value)
            if (i == count - 1 || fraction <= 1f) {
                val result = untransform(transformedMaps[i].interpolate(fraction))
                return if (clamp) result.coerceIn(lo, hi) else result
            }
            i++
        }
        error("Unreachable: interpolators is guaranteed non-empty.")
    }
}

/** Returns the segment index in `0..values.size - 2` containing [x], clamped to the ends for extrapolation. */
private fun segmentIndex(values: List<Float>, x: Float): Int {
    val last = values.size - 2
    if (x <= values[0]) return 0
    if (x >= values[values.size - 1]) return last
    var i = 1
    while (i < values.size - 1 && x >= values[i]) i++
    return i - 1
}

private fun List<Float>.isStrictlyAscending(): Boolean =
    asSequence().zipWithNext().all { (a, b) -> a < b }

private fun List<Float>.isStrictlyDescending(): Boolean =
    asSequence().zipWithNext().all { (a, b) -> a > b }
