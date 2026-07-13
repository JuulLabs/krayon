package com.juul.krayon.collections

import kotlin.math.sqrt

/** See equivalent [d3 function](https://github.com/d3/d3-array#sum). */
public inline fun <T> Iterable<T>.sum(selector: (T) -> Double?): Double {
    var sum = 0.0
    for (element in this) {
        val value = selector(element) ?: continue
        if (!value.isNaN()) sum += value
    }
    return sum
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#mean). */
public inline fun <T> Iterable<T>.mean(selector: (T) -> Double?): Double? {
    var count = 0
    var sum = 0.0
    for (element in this) {
        val value = selector(element) ?: continue
        if (!value.isNaN()) {
            count++
            sum += value
        }
    }
    return if (count > 0) sum / count else null
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#count). */
public inline fun <T> Iterable<T>.count(selector: (T) -> Double?): Int {
    var count = 0
    for (element in this) {
        val value = selector(element) ?: continue
        if (!value.isNaN()) count++
    }
    return count
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#variance). */
public inline fun <T> Iterable<T>.variance(selector: (T) -> Double?): Double? {
    var count = 0
    var mean = 0.0
    var sum = 0.0
    for (element in this) {
        val value = selector(element) ?: continue
        if (!value.isNaN()) {
            val delta = value - mean
            mean += delta / ++count
            sum += delta * (value - mean)
        }
    }
    return if (count > 1) sum / (count - 1) else null
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#deviation). */
public inline fun <T> Iterable<T>.deviation(selector: (T) -> Double?): Double? {
    val variance = variance(selector) ?: return null
    return sqrt(variance)
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#mode). */
public inline fun <T> Iterable<T>.mode(selector: (T) -> Double?): Double? {
    val counts = HashMap<Double, Int>()
    for (element in this) {
        val value = selector(element) ?: continue
        if (!value.isNaN()) counts[value] = (counts[value] ?: 0) + 1
    }
    var modeValue: Double? = null
    var modeCount = 0
    for ((value, count) in counts) {
        if (count > modeCount) {
            modeCount = count
            modeValue = value
        }
    }
    return modeValue
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#cumsum). */
public inline fun <T> Iterable<T>.cumsum(selector: (T) -> Double?): List<Double> {
    var sum = 0.0
    return map { element ->
        val value = selector(element)
        if (value != null && !value.isNaN()) sum += value
        sum
    }
}
