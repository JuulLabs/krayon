package com.juul.krayon.scale

/**
 * Maps a continuous [domain] to a discrete [range] by dividing the domain into uniform segments, mirroring
 * [d3's quantize scale](https://github.com/d3/d3-scale#quantize-scales).
 *
 * The [domain] is the two-element extent `[x0, x1]`; the [range] is a list of discrete output values.
 */
public class QuantizeScale<R> internal constructor(
    public val domain: List<Float>,
    public val range: List<R>,
) : Scale<Float, R> {

    init {
        require(domain.size == 2) { "Domain must consist of exactly 2 values, but was ${domain.size}." }
        require(range.isNotEmpty()) { "Range must consist of at least 1 value." }
    }

    private val x0 = domain[0]
    private val x1 = domain[1]
    private val n = range.size - 1

    /** The interior threshold values that partition the domain. */
    public val thresholds: List<Float> = List(n) { i -> ((i + 1) * x1 - (i - n) * x0) / (n + 1) }

    override fun scale(input: Float): R = range[bisectRight(thresholds, input, 0, n)]

    /** Returns the domain extent `[lower, upper]` mapped to [value]. */
    public fun invertExtent(value: R): Pair<Float, Float> {
        val i = range.indexOf(value)
        return when {
            i < 0 -> Pair(Float.NaN, Float.NaN)
            i < 1 -> Pair(x0, thresholds[0])
            i >= n -> Pair(thresholds[n - 1], x1)
            else -> Pair(thresholds[i - 1], thresholds[i])
        }
    }

    public fun domain(domain: List<Float>): QuantizeScale<R> = QuantizeScale(domain, range)

    public fun <R2> range(range: List<R2>): QuantizeScale<R2> = QuantizeScale(domain, range)
}

/** Creates a [QuantizeScale] with the given [domain] extent and [range]. */
public fun <R> quantizeScale(
    domain: List<Float> = listOf(0f, 1f),
    range: List<R>,
): QuantizeScale<R> = QuantizeScale(domain, range)
