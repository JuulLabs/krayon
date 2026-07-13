package com.juul.krayon.collections

import kotlin.math.abs

/**
 * An accumulator that sums doubles without accruing rounding error, using Shewchuk's algorithm for
 * exactly rounded floating-point summation.
 *
 * Ported from d3-array's `Adder`, in turn from
 * [CPython's math.fsum](https://github.com/python/cpython/blob/main/Modules/mathmodule.c).
 */
public class Adder {

    private val partials = DoubleArray(32)
    private var partialCount = 0

    /** Adds [value] to the running total and returns this accumulator for chaining. */
    public fun add(value: Double): Adder {
        var carry = value
        var target = 0
        var source = 0
        while (source < partialCount && source < partials.size) {
            val partial = partials[source]
            val sum = carry + partial
            val error = if (abs(carry) < abs(partial)) carry - (sum - partial) else partial - (sum - carry)
            if (error != 0.0) partials[target++] = error
            carry = sum
            source++
        }
        partials[target] = carry
        partialCount = target + 1
        return this
    }

    /** The current exactly-rounded total. */
    public fun value(): Double {
        var index = partialCount
        var high = 0.0
        if (index > 0) {
            high = partials[--index]
            var low = 0.0
            while (index > 0) {
                val previousHigh = high
                low = partials[--index]
                high = previousHigh + low
                low -= high - previousHigh
                if (low != 0.0) break
            }
            val roundsToEven = index > 0 &&
                ((low < 0.0 && partials[index - 1] < 0.0) || (low > 0.0 && partials[index - 1] > 0.0))
            if (roundsToEven) {
                val doubledLow = low * 2
                val rounded = high + doubledLow
                if (doubledLow == rounded - high) high = rounded
            }
        }
        return high
    }
}

/** The sum of all values, computed without rounding error via [Adder]. `NaN` values are ignored. */
public fun Iterable<Double>.preciseSum(): Double {
    val adder = Adder()
    for (value in this) if (!value.isNaN()) adder.add(value)
    return adder.value()
}

/**
 * The running totals of the values, each computed without rounding error via [Adder]. `NaN` values
 * contribute zero.
 */
public fun Iterable<Double>.preciseCumulativeSums(): List<Double> {
    val adder = Adder()
    return map { adder.add(if (it.isNaN()) 0.0 else it).value() }
}
