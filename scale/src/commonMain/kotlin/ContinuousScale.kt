package com.juul.krayon.scale

import com.juul.krayon.color.Color
import com.juul.krayon.interpolate.Interpolator
import com.juul.krayon.interpolate.Inverter
import com.juul.krayon.interpolate.interpolator
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.jvm.JvmName

private val DEFAULT_DOMAIN = listOf(0f, 1f)
private val DEFAULT_RANGE = listOf(0f, 1f)

/** Creates a scale with domain and range both set to [0f, 1f]. */
public fun scale(
    domain: List<Float> = DEFAULT_DOMAIN,
    range: List<Float> = DEFAULT_RANGE,
): ContinuousScale<Float, Float> = ContinuousScale(
    domain,
    range,
    getInverter = ::interpolator,
    getInterpolator = ::interpolator
)

// Syntax sugar for creating a new scale with a modifier domain and range, when the interpolator type is built in.
// If Kotlin ever allows anonymous sum types, OR if they allow defining traits external to a class, then we can clean this up.

// Vararg domains
public fun <R> ContinuousScale<*, R>.domain(vararg domain: Int): ContinuousScale<Int, R> = domain(domain.toList())
public fun <R> ContinuousScale<*, R>.domain(vararg domain: Float): ContinuousScale<Float, R> = domain(domain.toList())
public fun <R> ContinuousScale<*, R>.domain(vararg domain: Double): ContinuousScale<Double, R> = domain(domain.toList())
public fun <R> ContinuousScale<*, R>.domain(vararg domain: Instant): ContinuousScale<Instant, R> = domain(domain.toList())
public fun <R> ContinuousScale<*, R>.domain(vararg domain: LocalDateTime): ContinuousScale<LocalDateTime, R> = domain(domain.toList())

// List domains
@JvmName("domainInt")
public fun <R> ContinuousScale<*, R>.domain(domain: List<Int>): ContinuousScale<Int, R> = domain(domain.toList(), ::interpolator)

@JvmName("domainFloat")
public fun <R> ContinuousScale<*, R>.domain(domain: List<Float>): ContinuousScale<Float, R> = domain(domain.toList(), ::interpolator)

@JvmName("domainDouble")
public fun <R> ContinuousScale<*, R>.domain(domain: List<Double>): ContinuousScale<Double, R> = domain(domain.toList(), ::interpolator)

@JvmName("domainInstant")
public fun <R> ContinuousScale<*, R>.domain(domain: List<Instant>): ContinuousScale<Instant, R> = domain(domain.toList(), ::interpolator)

@JvmName("domainLocalDateTime")
public fun <R> ContinuousScale<*, R>.domain(domain: List<LocalDateTime>): ContinuousScale<LocalDateTime, R> = domain(domain.toList(), ::interpolator)

// Vararg ranges
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(vararg range: Int): ContinuousScale<D, Int> = range(range.toList())
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(vararg range: Float): ContinuousScale<D, Float> = range(range.toList())
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(vararg range: Double): ContinuousScale<D, Double> = range(range.toList())
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(vararg range: Instant): ContinuousScale<D, Instant> = range(range.toList())
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(vararg range: LocalDateTime): ContinuousScale<D, LocalDateTime> = range(range.toList())

// No vararg on explicit value classes, so no vararg syntax sugar for `Color`, sorry.
// public fun <D : Comparable<D>> ContinuousScale<D, *>.range(vararg range: Color): ContinuousScale<D, Color> = range(range.toList())

// List ranges
@JvmName("rangeInt")
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(range: List<Int>): ContinuousScale<D, Int> = range(range, ::interpolator)

@JvmName("rangeFloat")
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(range: List<Float>): ContinuousScale<D, Float> = range(range, ::interpolator)

@JvmName("rangeDouble")
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(range: List<Double>): ContinuousScale<D, Double> = range(range, ::interpolator)

@JvmName("rangeInstant")
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(range: List<Instant>): ContinuousScale<D, Instant> = range(range, ::interpolator)

@JvmName("rangeLocalDateTime")
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(range: List<LocalDateTime>): ContinuousScale<D, LocalDateTime> = range(range, ::interpolator)

@JvmName("rangeColor")
public fun <D : Comparable<D>> ContinuousScale<D, *>.range(range: List<Color>): ContinuousScale<D, Color> = range(range, ::interpolator)

public class ContinuousScale<D : Comparable<D>, R> internal constructor(
    private val domain: List<D>,
    private val range: List<R>,
    private val getInverter: (start: D, stop: D) -> Inverter<D>,
    private val getInterpolator: (start: R, stop: R) -> Interpolator<R>,
) : Scale<D, R> {

    /** Wrapper for internal sanity-checked state. This is to allow a fluent builder-like API without an explicit `build` function. */
    private inner class State {
        init {
            require(domain.size >= 2) { "Domain must consist of at least 2 values, but was ${domain.size}." }
            require(range.size >= 2) { "Range must consist of at least 2 values, but was ${domain.size}." }
            require(domain.size == range.size) { "Domain and range must have the same number of values, but domain was ${domain.size} and range was ${range.size}." }
            require(domain.isAscending() || domain.isDescending()) { "Domain must be strictly ascending or strictly descending." }
        }

        private val isReversed = domain[1] < domain[0]

        val sortedDomain = if (!isReversed) domain else domain.asReversed()
        val sortedRange = if (!isReversed) range else range.asReversed()

        val subdomains = sortedDomain.zipWithNext { start, stop -> start..stop }
        val inverters = sortedDomain.zipWithNext(getInverter)
        val interpolators = sortedRange.zipWithNext(getInterpolator)
    }

    private val state by lazy { State() }

    override fun scale(input: D): R = with(state) {
        val index = subdomains.binarySearch { subdomain ->
            when {
                input > subdomain.endInclusive -> -1
                input < subdomain.start -> 1
                else -> 0
            }
        }
        check(index >= 0) { "No matching subdomain found for input $input in subdomains $subdomains." }
        return interpolators[index].interpolate(inverters[index].invert(input))
    }

    public fun <D2 : Comparable<D2>> domain(
        domain: List<D2>,
        getInverter: (start: D2, stop: D2) -> Inverter<D2>,
    ): ContinuousScale<D2, R> = ContinuousScale(domain, range, getInverter, getInterpolator)

    public fun <R2> range(
        range: List<R2>,
        getInterpolator: (start: R2, stop: R2) -> Interpolator<R2>,
    ): ContinuousScale<D, R2> = ContinuousScale(domain, range, getInverter, getInterpolator)
}

private fun <T : Comparable<T>> Iterable<T>.isAscending(): Boolean =
    asSequence().zipWithNext().all { (left, right) -> left < right }

private fun <T : Comparable<T>> Iterable<T>.isDescending(): Boolean =
    asSequence().zipWithNext().all { (left, right) -> left > right }
