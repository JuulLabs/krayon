package com.juul.krayon.scale

/**
 * Returns a copy of [domain] with its extreme endpoints extended outward to "nice" round values for approximately
 * [count] ticks, preserving domain order and any interior values. Mirrors the endpoint handling of
 * [d3's linear nice](https://github.com/d3/d3-scale#continuous_nice).
 */
internal fun niceLinearDomain(domain: List<Float>, count: Int): List<Float> {
    val result = domain.toMutableList()
    var lowIndex = 0
    var highIndex = domain.lastIndex
    if (domain[highIndex] < domain[lowIndex]) {
        val swap = lowIndex
        lowIndex = highIndex
        highIndex = swap
    }
    val niced = nice(domain[lowIndex], domain[highIndex], count)
    result[lowIndex] = niced.first()
    result[highIndex] = niced.last()
    return result
}

/** Returns linear ticks spanning the extreme endpoints of [domain], respecting domain order. */
internal fun linearTicks(domain: List<Float>, count: Int): List<Float> =
    ticks(domain.first(), domain.last(), count)
