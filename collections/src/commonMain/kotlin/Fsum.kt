package com.juul.krayon.collections

import kotlin.math.abs

/**
 * A full-precision floating-point accumulator using Shewchuk's algorithm for exactly rounded
 * summation. See equivalent [d3 class](https://github.com/d3/d3-array#Adder).
 */
public class Adder {

    private var partials = DoubleArray(32)
    private var count = 0

    /** Adds [value] to the accumulator and returns this [Adder] for chaining. */
    public fun add(value: Double): Adder {
        var x = value
        val p = partials
        var i = 0
        var j = 0
        while (j < count && j < 32) {
            val y = p[j]
            val hi = x + y
            val lo = if (abs(x) < abs(y)) x - (hi - y) else y - (hi - x)
            if (lo != 0.0) p[i++] = lo
            x = hi
            j++
        }
        p[i] = x
        count = i + 1
        return this
    }

    /** Returns the current exactly-rounded sum. */
    public fun value(): Double {
        val p = partials
        var n = count
        var hi = 0.0
        if (n > 0) {
            hi = p[--n]
            var lo = 0.0
            while (n > 0) {
                val x = hi
                val y = p[--n]
                hi = x + y
                lo = y - (hi - x)
                if (lo != 0.0) break
            }
            if (n > 0 && ((lo < 0.0 && p[n - 1] < 0.0) || (lo > 0.0 && p[n - 1] > 0.0))) {
                val y = lo * 2
                val x = hi + y
                if (y == x - hi) hi = x
            }
        }
        return hi
    }
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#fsum). */
public inline fun <T> Iterable<T>.fsum(selector: (T) -> Double?): Double {
    val adder = Adder()
    for (element in this) {
        val value = selector(element) ?: continue
        if (!value.isNaN()) adder.add(value)
    }
    return adder.value()
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#fcumsum). */
public inline fun <T> Iterable<T>.fcumsum(selector: (T) -> Double?): List<Double> {
    val adder = Adder()
    return map { element ->
        val value = selector(element)
        adder.add(if (value == null || value.isNaN()) 0.0 else value).value()
    }
}
