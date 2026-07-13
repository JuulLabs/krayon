package com.juul.krayon.scale

import com.juul.krayon.interpolate.Interpolator
import com.juul.krayon.interpolate.interpolator
import kotlin.math.abs
import kotlin.math.expm1
import kotlin.math.ln1p
import kotlin.math.sign

/**
 * A continuous scale with a symmetric-log transform, mirroring
 * [d3's symlog scale](https://github.com/d3/d3-scale#symlog-scales).
 *
 * Unlike log scales, the domain may span zero. [constant] controls the size of the near-zero linear region.
 */
public class SymlogScale<R> internal constructor(
    public val domain: List<Float>,
    public val range: List<R>,
    public val constant: Float,
    public val clamp: Boolean,
    private val getInterpolator: (R, R) -> Interpolator<R>,
) : Scale<Float, R> {

    private val transformed = TransformedScale(
        domain = domain,
        range = range,
        transform = transformSymlog(constant),
        untransform = transformSymexp(constant),
        clamp = clamp,
        getInterpolator = getInterpolator,
    )

    override fun scale(input: Float): R = transformed.scale(input)

    public fun invert(value: R): Float = transformed.invert(value)

    public fun ticks(count: Int = 10): List<Float> = linearTicks(domain, count)

    public fun tickFormat(count: Int = 10): (Float) -> String = tickFormat(domain.first(), domain.last(), count)

    public fun nice(count: Int = 10): SymlogScale<R> =
        SymlogScale(niceLinearDomain(domain, count), range, constant, clamp, getInterpolator)

    public fun constant(constant: Float): SymlogScale<R> = SymlogScale(domain, range, constant, clamp, getInterpolator)

    public fun domain(domain: List<Float>): SymlogScale<R> = SymlogScale(domain, range, constant, clamp, getInterpolator)

    public fun <R2> range(
        range: List<R2>,
        getInterpolator: (R2, R2) -> Interpolator<R2>,
    ): SymlogScale<R2> = SymlogScale(domain, range, constant, clamp, getInterpolator)

    public fun clamp(clamp: Boolean = true): SymlogScale<R> = SymlogScale(domain, range, constant, clamp, getInterpolator)
}

/** Creates a [SymlogScale] with the given [domain] and [range]; [constant] defaults to 1. */
public fun symlogScale(
    domain: List<Float> = listOf(0f, 1f),
    range: List<Float> = listOf(0f, 1f),
    constant: Float = 1f,
): SymlogScale<Float> = SymlogScale(domain, range, constant, clamp = false, getInterpolator = ::interpolator)

private fun transformSymlog(constant: Float): (Float) -> Float = { x ->
    sign(x) * ln1p(abs(x / constant))
}

private fun transformSymexp(constant: Float): (Float) -> Float = { x ->
    sign(x) * expm1(abs(x)) * constant
}
