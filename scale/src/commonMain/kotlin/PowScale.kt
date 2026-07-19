package com.juul.krayon.scale

import com.juul.krayon.interpolate.Interpolator
import com.juul.krayon.interpolate.interpolator
import kotlin.math.pow

/**
 * A continuous scale with an exponential transform, mirroring [d3's pow scale](https://github.com/d3/d3-scale#pow-scales).
 *
 * Unlike log scales, the domain may include negative values and zero (the transform preserves sign).
 */
public class PowScale<R> internal constructor(
    public val domain: List<Float>,
    public val range: List<R>,
    public val exponent: Float,
    public val clamp: Boolean,
    private val getInterpolator: (R, R) -> Interpolator<R>,
) : Scale<Float, R> {

    private val transformed = TransformedScale(
        domain = domain,
        range = range,
        transform = transformPow(exponent),
        untransform = transformPow(1f / exponent),
        clamp = clamp,
        getInterpolator = getInterpolator,
    )

    override fun scale(input: Float): R = transformed.scale(input)

    public fun invert(value: R): Float = transformed.invert(value)

    public fun ticks(count: Int = 10): List<Float> = linearTicks(domain, count)

    public fun tickFormat(count: Int = 10): (Float) -> String = tickFormat(domain.first(), domain.last(), count)

    public fun nice(count: Int = 10): PowScale<R> =
        PowScale(niceLinearDomain(domain, count), range, exponent, clamp, getInterpolator)

    public fun exponent(exponent: Float): PowScale<R> = PowScale(domain, range, exponent, clamp, getInterpolator)

    public fun domain(domain: List<Float>): PowScale<R> = PowScale(domain, range, exponent, clamp, getInterpolator)

    public fun <R2> range(
        range: List<R2>,
        getInterpolator: (R2, R2) -> Interpolator<R2>,
    ): PowScale<R2> = PowScale(domain, range, exponent, clamp, getInterpolator)

    public fun clamp(clamp: Boolean = true): PowScale<R> = PowScale(domain, range, exponent, clamp, getInterpolator)
}

/** Creates a [PowScale] with the given [domain], [range], and [exponent] (default 1). */
public fun powScale(
    domain: List<Float> = listOf(0f, 1f),
    range: List<Float> = listOf(0f, 1f),
    exponent: Float = 1f,
): PowScale<Float> = PowScale(domain, range, exponent, clamp = false, getInterpolator = ::interpolator)

/** Creates a [PowScale] with an [exponent] of 0.5 (a square-root scale). */
public fun sqrtScale(
    domain: List<Float> = listOf(0f, 1f),
    range: List<Float> = listOf(0f, 1f),
): PowScale<Float> = powScale(domain, range, exponent = 0.5f)

private fun transformPow(exponent: Float): (Float) -> Float = { x ->
    if (x < 0f) -((-x).pow(exponent)) else x.pow(exponent)
}
