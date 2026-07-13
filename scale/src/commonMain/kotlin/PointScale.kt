package com.juul.krayon.scale

import kotlin.math.floor
import kotlin.math.max

/**
 * Maps a discrete [domain] to evenly spaced points across a continuous [range], mirroring
 * [d3's point scale](https://github.com/d3/d3-scale#point-scales).
 *
 * Point scales are band scales with an inner padding of 1 (so [bandwidth] is 0). [padding] is the outer padding and
 * [step] is the distance between adjacent points.
 */
public class PointScale<D> internal constructor(
    public val domain: List<D>,
    public val range: List<Float>,
    public val padding: Float,
    public val align: Float,
    public val round: Boolean,
) : Scale<D, Float> {

    init {
        require(range.size == 2) { "Range must consist of exactly 2 values, but was ${range.size}." }
    }

    private inner class State {
        val positions: Map<D, Float>
        val step: Float

        init {
            val n = domain.size
            val r0 = range[0]
            val r1 = range[1]
            val reverse = r1 < r0
            val lo = if (reverse) r1 else r0
            val hi = if (reverse) r0 else r1

            var computedStep = (hi - lo) / max(1f, n - 1 + padding * 2)
            if (round) computedStep = floor(computedStep)

            var start = lo + (hi - lo - computedStep * (n - 1)) * align
            if (round) start = floor(start + 0.5f)

            val values = List(n) { i -> start + computedStep * i }
            val ordered = if (reverse) values.asReversed() else values
            positions = domain.indices.associate { i -> domain[i] to ordered[i] }
            step = computedStep
        }
    }

    private val state by lazy { State() }

    /** The distance between adjacent points. */
    public val step: Float get() = state.step

    /** Always 0 for point scales; provided for parity with band scales. */
    public val bandwidth: Float get() = 0f

    override fun scale(input: D): Float =
        state.positions[input] ?: throw IllegalArgumentException("Input $input is not in the domain $domain.")

    public fun domain(domain: List<D>): PointScale<D> = PointScale(domain, range, padding, align, round)

    public fun range(range: List<Float>): PointScale<D> = PointScale(domain, range, padding, align, round)

    public fun padding(padding: Float): PointScale<D> = PointScale(domain, range, padding, align, round)

    public fun align(align: Float): PointScale<D> = PointScale(domain, range, padding, align.coerceIn(0f, 1f), round)

    public fun round(round: Boolean): PointScale<D> = PointScale(domain, range, padding, align, round)
}

/** Creates a [PointScale] with the given [domain] and [range]. */
public fun <D> pointScale(
    domain: List<D> = emptyList(),
    range: List<Float> = listOf(0f, 1f),
): PointScale<D> = PointScale(domain, range, padding = 0f, align = 0.5f, round = false)
