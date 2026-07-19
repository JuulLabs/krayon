package com.juul.krayon.scale

import kotlin.math.floor
import kotlin.math.max

/**
 * Maps a discrete [domain] to a continuous [range], mirroring [d3's band scale](https://github.com/d3/d3-scale#band-scales).
 *
 * Band scales are typically used for bar charts: the [range] is divided into uniform bands (one per domain value)
 * separated by padding. Each input maps to the start of its band; [bandwidth] gives the band width and [step] the
 * distance between the starts of adjacent bands.
 */
public class BandScale<D> internal constructor(
    public val domain: List<D>,
    public val range: List<Float>,
    public val paddingInner: Float,
    public val paddingOuter: Float,
    public val align: Float,
    public val round: Boolean,
) : Scale<D, Float> {

    init {
        require(range.size == 2) { "Range must consist of exactly 2 values, but was ${range.size}." }
    }

    private inner class State {
        val positions: Map<D, Float>
        val step: Float
        val bandwidth: Float

        init {
            val n = domain.size
            val r0 = range[0]
            val r1 = range[1]
            val reverse = r1 < r0
            val lo = if (reverse) r1 else r0
            val hi = if (reverse) r0 else r1

            var computedStep = (hi - lo) / max(1f, n - paddingInner + paddingOuter * 2)
            if (round) computedStep = floor(computedStep)

            var start = lo + (hi - lo - computedStep * (n - paddingInner)) * align
            var computedBandwidth = computedStep * (1 - paddingInner)
            if (round) {
                start = floor(start + 0.5f)
                computedBandwidth = floor(computedBandwidth + 0.5f)
            }

            val values = List(n) { i -> start + computedStep * i }
            val ordered = if (reverse) values.asReversed() else values
            positions = domain.indices.associate { i -> domain[i] to ordered[i] }
            step = computedStep
            bandwidth = computedBandwidth
        }
    }

    private val state by lazy { State() }

    /** The distance between the starts of adjacent bands. */
    public val step: Float get() = state.step

    /** The width of each band. */
    public val bandwidth: Float get() = state.bandwidth

    override fun scale(input: D): Float =
        state.positions[input] ?: throw IllegalArgumentException("Input $input is not in the domain $domain.")

    public fun domain(domain: List<D>): BandScale<D> =
        BandScale(domain, range, paddingInner, paddingOuter, align, round)

    public fun range(range: List<Float>): BandScale<D> =
        BandScale(domain, range, paddingInner, paddingOuter, align, round)

    public fun paddingInner(padding: Float): BandScale<D> =
        BandScale(domain, range, padding.coerceIn(0f, 1f), paddingOuter, align, round)

    public fun paddingOuter(padding: Float): BandScale<D> =
        BandScale(domain, range, paddingInner, padding, align, round)

    /** Sets both [paddingInner] (clamped to `0..1`) and [paddingOuter] to [padding], matching d3's `padding`. */
    public fun padding(padding: Float): BandScale<D> =
        BandScale(domain, range, padding.coerceIn(0f, 1f), padding, align, round)

    public fun align(align: Float): BandScale<D> =
        BandScale(domain, range, paddingInner, paddingOuter, align.coerceIn(0f, 1f), round)

    public fun round(round: Boolean): BandScale<D> =
        BandScale(domain, range, paddingInner, paddingOuter, align, round)
}

/** Creates a [BandScale] with the given [domain] and [range]. */
public fun <D> bandScale(
    domain: List<D> = emptyList(),
    range: List<Float> = listOf(0f, 1f),
): BandScale<D> = BandScale(domain, range, paddingInner = 0f, paddingOuter = 0f, align = 0.5f, round = false)
