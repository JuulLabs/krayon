package com.juul.krayon.scale

import kotlin.math.floor

/**
 * Maps a continuous sample [domain] to a discrete [range] using quantiles, mirroring
 * [d3's quantile scale](https://github.com/d3/d3-scale#quantile-scales).
 *
 * The [domain] is a set of sample values (sorted internally); the [range] is the list of discrete output values. The
 * computed [quantiles] partition the samples so that each range value receives an equal number of samples.
 */
public class QuantileScale<R> internal constructor(
    domain: List<Float>,
    public val range: List<R>,
) : Scale<Float, R> {

    init {
        require(domain.isNotEmpty()) { "Domain must consist of at least 1 value." }
        require(range.isNotEmpty()) { "Range must consist of at least 1 value." }
    }

    /** The sample domain, sorted ascending. */
    public val domain: List<Float> = domain.sorted()

    /** The interior quantile boundaries between adjacent range values. */
    public val quantiles: List<Float> = List(range.size - 1) { i ->
        quantileSorted(this.domain, (i + 1).toFloat() / range.size)
    }

    override fun scale(input: Float): R = range[bisectRight(quantiles, input)]

    /** Returns the domain extent `[lower, upper]` mapped to [value]. */
    public fun invertExtent(value: R): Pair<Float, Float> {
        val i = range.indexOf(value)
        if (i < 0) return Pair(Float.NaN, Float.NaN)
        val lower = if (i > 0) quantiles[i - 1] else domain.first()
        val upper = if (i < quantiles.size) quantiles[i] else domain.last()
        return Pair(lower, upper)
    }

    public fun domain(domain: List<Float>): QuantileScale<R> = QuantileScale(domain, range)

    public fun <R2> range(range: List<R2>): QuantileScale<R2> = QuantileScale(domain, range)
}

/** Creates a [QuantileScale] with the given sample [domain] and [range]. */
public fun <R> quantileScale(
    domain: List<Float>,
    range: List<R>,
): QuantileScale<R> = QuantileScale(domain, range)

/** The [p]-quantile of an ascending-sorted [values] list, matching [d3-array's quantileSorted](https://github.com/d3/d3-array#quantileSorted). */
private fun quantileSorted(values: List<Float>, p: Float): Float {
    val n = values.size
    if (p <= 0f || n < 2) return values[0]
    if (p >= 1f) return values[n - 1]
    val i = (n - 1) * p
    val i0 = floor(i).toInt()
    val value0 = values[i0]
    val value1 = values[i0 + 1]
    return value0 + (value1 - value0) * (i - i0)
}
