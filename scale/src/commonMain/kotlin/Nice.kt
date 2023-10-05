package com.juul.krayon.scale

import kotlin.math.ceil
import kotlin.math.floor

/** See equivalent [d3 function](https://github.com/d3/d3-array#nice). */
public fun nice(start: Float, stop: Float, count: Int): List<Float> =
    nice(start.toDouble(), stop.toDouble(), count).map { it.toFloat() }

/** See equivalent [d3 function](https://github.com/d3/d3-array#nice). */
public fun nice(start: Double, stop: Double, count: Int): List<Double> {
    var nextStart = start
    var nextStop = stop
    var previousIncrement: Int? = null
    while (true) {
        val increment = tickIncrement(nextStart, nextStop, count)
        if (increment == previousIncrement || increment == 0) {
            return listOf(nextStart, nextStop)
        } else if (increment > 0) {
            nextStart = floor(nextStart / increment) * increment
            nextStop = ceil(nextStop / increment) * increment
        } else {
            nextStart = ceil(nextStart * increment) / increment
            nextStop = floor(nextStop * increment) / increment
        }
        previousIncrement = increment
    }
}
