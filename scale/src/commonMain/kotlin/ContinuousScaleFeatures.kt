@file:OptIn(ExperimentalTime::class)

package com.juul.krayon.scale

import kotlinx.datetime.LocalDateTime
import kotlin.jvm.JvmName
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Returns approximately [count] representative tick values spanning this scale's [ContinuousScale.domain]. */
@JvmName("ticksFloat")
public fun ContinuousScale<Float, *>.ticks(count: Int = 10): List<Float> =
    ticks(domain.first(), domain.last(), count)

/** Returns approximately [count] representative tick values spanning this scale's [ContinuousScale.domain]. */
@JvmName("ticksDouble")
public fun ContinuousScale<Double, *>.ticks(count: Int = 10): List<Double> =
    ticks(domain.first(), domain.last(), count)

/** Returns approximately [count] representative tick values spanning this scale's [ContinuousScale.domain]. */
@JvmName("ticksLocalDateTime")
public fun ContinuousScale<LocalDateTime, *>.ticks(count: Int = 10): List<LocalDateTime> =
    ticks(domain.first(), domain.last(), count)

/** Returns approximately [count] representative tick values spanning this scale's [ContinuousScale.domain]. */
@JvmName("ticksInstant")
public fun ContinuousScale<Instant, *>.ticks(count: Int = 10): List<Instant> =
    InstantTicker.ticks(domain.first(), domain.last(), count)

/** Returns a copy with its domain endpoints extended to nice round values for approximately [count] ticks. */
@JvmName("niceFloat")
public fun <R> ContinuousScale<Float, R>.nice(count: Int = 10): ContinuousScale<Float, R> =
    domain(niceLinearDomain(domain, count))

/** Returns a copy with its domain endpoints extended to nice round values for approximately [count] ticks. */
@JvmName("niceDouble")
public fun <R> ContinuousScale<Double, R>.nice(count: Int = 10): ContinuousScale<Double, R> {
    val values = domain
    var lowIndex = 0
    var highIndex = values.lastIndex
    if (values[highIndex] < values[lowIndex]) {
        val swap = lowIndex
        lowIndex = highIndex
        highIndex = swap
    }
    val niced = nice(values[lowIndex], values[highIndex], count)
    val result = values.toMutableList()
    result[lowIndex] = niced.first()
    result[highIndex] = niced.last()
    return domain(result)
}

/** Returns a formatter for this scale's ticks. See [tickFormat]. */
@JvmName("tickFormatFloat")
public fun ContinuousScale<Float, *>.tickFormat(count: Int = 10): (Float) -> String =
    tickFormat(domain.first(), domain.last(), count)

/** Returns a formatter for this scale's ticks. See [tickFormat]. */
@JvmName("tickFormatDouble")
public fun ContinuousScale<Double, *>.tickFormat(count: Int = 10): (Double) -> String =
    tickFormat(domain.first(), domain.last(), count)
