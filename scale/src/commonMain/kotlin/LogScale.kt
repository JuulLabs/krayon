package com.juul.krayon.scale

import com.juul.krayon.interpolate.Interpolator
import com.juul.krayon.interpolate.interpolator
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow

private val E = kotlin.math.E.toFloat()

/**
 * A continuous scale with a logarithmic transform, mirroring [d3's log scale](https://github.com/d3/d3-scale#log-scales).
 *
 * The domain must be strictly positive or strictly negative (it may not span or include zero). The [base] affects only
 * [ticks], [tickFormat], and [nice]; the underlying interpolation uses the natural logarithm regardless of base.
 */
public class LogScale<R> internal constructor(
    public val domain: List<Float>,
    public val range: List<R>,
    public val base: Float,
    public val clamp: Boolean,
    private val getInterpolator: (R, R) -> Interpolator<R>,
) : Scale<Float, R> {

    private val negative = domain.first() < 0f
    private val logs: (Float) -> Float = if (negative) reflect(logp(base)) else logp(base)
    private val pows: (Float) -> Float = if (negative) reflect(powp(base)) else powp(base)

    private val transformed = TransformedScale(
        domain = domain,
        range = range,
        transform = if (negative) { x -> -ln(-x) } else { x -> ln(x) },
        untransform = if (negative) { x -> -exp(-x) } else { x -> exp(x) },
        clamp = clamp,
        getInterpolator = getInterpolator,
    )

    override fun scale(input: Float): R = transformed.scale(input)

    public fun invert(value: R): Float = transformed.invert(value)

    public fun ticks(count: Int = 10): List<Float> {
        var u = domain.first()
        var v = domain.last()
        val reverse = v < u
        if (reverse) {
            val swap = u
            u = v
            v = swap
        }

        val i = logs(u)
        val j = logs(v)
        val result = ArrayList<Float>()
        val isIntegerBase = base % 1f == 0f
        val intBase = base.toInt()

        if (isIntegerBase && (j - i) < count) {
            var p = floor(i)
            val end = ceil(j)
            if (u > 0f) {
                while (p <= end) {
                    var k = 1
                    while (k < intBase) {
                        val t = if (p < 0f) k / pows(-p) else k * pows(p)
                        if (t in u..v) result.add(t)
                        k++
                    }
                    p += 1f
                }
            } else {
                while (p <= end) {
                    var k = intBase - 1
                    while (k >= 1) {
                        val t = if (p > 0f) k / pows(-p) else k * pows(p)
                        if (t in u..v) result.add(t)
                        k--
                    }
                    p += 1f
                }
            }
            if (result.size * 2 < count) {
                result.clear()
                result.addAll(ticks(u, v, count))
            }
        } else {
            val c = minOf(j - i, count.toFloat()).toInt().coerceAtLeast(1)
            ticks(i, j, c).mapTo(result, pows)
        }

        return if (reverse) result.asReversed() else result
    }

    public fun tickFormat(count: Int = 10): (Float) -> String {
        val cutoff = maxOf(1f, base * count / ticks(count).size)
        return format@{ d ->
            var mantissa = d / pows((logs(d)).roundToNearest())
            if (mantissa * base < base - 0.5f) mantissa *= base
            if (mantissa <= cutoff) defaultNumberFormat(d) else ""
        }
    }

    public fun nice(): LogScale<R> {
        val niced = niceEndpoints(domain, floor = { pows(floor(logs(it))) }, ceil = { pows(ceil(logs(it))) })
        return LogScale(niced, range, base, clamp, getInterpolator)
    }

    public fun base(base: Float): LogScale<R> = LogScale(domain, range, base, clamp, getInterpolator)

    public fun domain(domain: List<Float>): LogScale<R> = LogScale(domain, range, base, clamp, getInterpolator)

    public fun <R2> range(
        range: List<R2>,
        getInterpolator: (R2, R2) -> Interpolator<R2>,
    ): LogScale<R2> = LogScale(domain, range, base, clamp, getInterpolator)

    public fun clamp(clamp: Boolean = true): LogScale<R> = LogScale(domain, range, base, clamp, getInterpolator)
}

/** Creates a [LogScale] with the given [domain] and [range]; [base] defaults to 10. */
public fun logScale(
    domain: List<Float> = listOf(1f, 10f),
    range: List<Float> = listOf(0f, 1f),
    base: Float = 10f,
): LogScale<Float> = LogScale(domain, range, base, clamp = false, getInterpolator = ::interpolator)

private fun logp(base: Float): (Float) -> Float {
    val denominator = ln(base)
    return when (base) {
        E -> { x -> ln(x) }
        10f -> { x -> log10(x) }
        2f -> { x -> log2(x) }
        else -> { x -> ln(x) / denominator }
    }
}

private fun powp(base: Float): (Float) -> Float = when (base) {
    E -> { x -> exp(x) }
    else -> { x -> base.pow(x) }
}

private fun reflect(f: (Float) -> Float): (Float) -> Float = { x -> -f(-x) }

private fun Float.roundToNearest(): Float = floor(this + 0.5f)

private fun niceEndpoints(
    domain: List<Float>,
    floor: (Float) -> Float,
    ceil: (Float) -> Float,
): List<Float> {
    val result = domain.toMutableList()
    var lowIndex = 0
    var highIndex = domain.lastIndex
    if (domain[highIndex] < domain[lowIndex]) {
        val swap = lowIndex
        lowIndex = highIndex
        highIndex = swap
    }
    result[lowIndex] = floor(domain[lowIndex])
    result[highIndex] = ceil(domain[highIndex])
    return result
}
