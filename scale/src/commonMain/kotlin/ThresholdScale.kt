package com.juul.krayon.scale

/**
 * Maps a continuous [domain] of thresholds to a discrete [range], mirroring
 * [d3's threshold scale](https://github.com/d3/d3-scale#threshold-scales).
 *
 * The [domain] must be sorted ascending and have one fewer value than the [range]; each domain value is the boundary
 * between adjacent range values.
 */
public class ThresholdScale<D : Comparable<D>, R> internal constructor(
    public val domain: List<D>,
    public val range: List<R>,
) : Scale<D, R> {

    private val n = minOf(domain.size, range.size - 1)

    init {
        require(range.isNotEmpty()) { "Range must consist of at least 1 value." }
    }

    override fun scale(input: D): R = range[bisectRight(domain, input, 0, n)]

    /** Returns the domain extent `[lower, upper)` mapped to [value], with `null` bounds where unbounded. */
    public fun invertExtent(value: R): Pair<D?, D?> {
        val i = range.indexOf(value)
        return Pair(domain.getOrNull(i - 1), domain.getOrNull(i))
    }

    public fun domain(domain: List<D>): ThresholdScale<D, R> = ThresholdScale(domain, range)

    public fun <R2> range(range: List<R2>): ThresholdScale<D, R2> = ThresholdScale(domain, range)
}

/** Creates a [ThresholdScale] with the given [domain] (thresholds) and [range]. */
public fun <D : Comparable<D>, R> thresholdScale(
    domain: List<D>,
    range: List<R>,
): ThresholdScale<D, R> = ThresholdScale(domain, range)
