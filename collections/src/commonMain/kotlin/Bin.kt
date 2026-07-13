package com.juul.krayon.collections

import com.juul.krayon.collections.internal.nice
import com.juul.krayon.collections.internal.tickIncrement
import com.juul.krayon.collections.internal.ticks
import kotlin.math.cbrt
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * A single bin produced by [bin]/[Binner]. Behaves as a [List] of the values it contains, with
 * [x0] (inclusive lower bound) and [x1] (upper bound; inclusive only for the last bin).
 */
public class Bin<out T> internal constructor(
    public val x0: Double,
    public val x1: Double,
    private val values: List<T>,
) : List<T> by values {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Bin<*>) return false
        return x0 == other.x0 && x1 == other.x1 && values == other.values
    }

    override fun hashCode(): Int {
        var result = x0.hashCode()
        result = 31 * result + x1.hashCode()
        result = 31 * result + values.hashCode()
        return result
    }

    override fun toString(): String = "Bin(x0=$x0, x1=$x1, values=$values)"
}

internal sealed interface ThresholdSpec {
    class Count(val strategy: (values: List<Double>, min: Double, max: Double) -> Int) : ThresholdSpec

    class Values(val strategy: (values: List<Double>, min: Double, max: Double) -> List<Double>) : ThresholdSpec
}

/**
 * Bins discrete samples into consecutive, non-overlapping intervals. Configured with a fluent
 * builder mirroring [d3's bin generator](https://github.com/d3/d3-array#bins).
 */
public class Binner<T> internal constructor(
    private val valueAccessor: (T) -> Double,
    private val domainAccessor: ((values: List<Double>) -> Pair<Double, Double>)?,
    private val thresholdSpec: ThresholdSpec,
) {

    /** Sets the accessor used to read the numeric value of each datum. */
    public fun value(selector: (T) -> Double): Binner<T> =
        Binner(selector, domainAccessor, thresholdSpec)

    /** Sets a custom domain function `[x0, x1]` computed from the mapped values. */
    public fun domain(selector: (values: List<Double>) -> Pair<Double, Double>): Binner<T> =
        Binner(valueAccessor, selector, thresholdSpec)

    /** Sets a fixed domain `[min, max]`. */
    public fun domain(min: Double, max: Double): Binner<T> =
        Binner(valueAccessor, { min to max }, thresholdSpec)

    /** Sets the approximate number of bins to produce. */
    public fun thresholds(count: Int): Binner<T> =
        Binner(valueAccessor, domainAccessor, ThresholdSpec.Count { _, _, _ -> count })

    /** Sets a strategy computing the approximate number of bins, e.g. [thresholdSturges]. */
    public fun thresholds(strategy: (values: List<Double>, min: Double, max: Double) -> Int): Binner<T> =
        Binner(valueAccessor, domainAccessor, ThresholdSpec.Count(strategy))

    /** Sets explicit threshold values used as bin boundaries. */
    public fun thresholds(values: List<Double>): Binner<T> =
        Binner(valueAccessor, domainAccessor, ThresholdSpec.Values { _, _, _ -> values })

    /** Computes the bins for [data]. */
    public operator fun invoke(data: Iterable<T>): List<Bin<T>> {
        val dataList = data.toList()
        val n = dataList.size
        val values = DoubleArray(n) { valueAccessor(dataList[it]) }
        val valueList = values.asList()

        val isExtent = domainAccessor == null
        val domain = domainAccessor?.invoke(valueList) ?: extent(valueList)
        var x0 = domain.first
        var x1 = domain.second
        var step = Double.NaN

        val computed: MutableList<Double> = when (val spec = thresholdSpec) {
            is ThresholdSpec.Values -> spec.strategy(valueList, x0, x1).toMutableList()
            is ThresholdSpec.Count -> {
                val originalMax = x1
                val count = spec.strategy(valueList, x0, x1)
                if (isExtent) {
                    val niced = nice(x0, x1, count)
                    x0 = niced.first
                    x1 = niced.second
                }
                val tz = ticks(x0, x1, count).toMutableList()
                if (tz.isNotEmpty() && tz.first() <= x0) step = tickIncrement(x0, x1, count)
                if (tz.isNotEmpty() && tz.last() >= x1) {
                    if (originalMax >= x1 && isExtent) {
                        val increment = tickIncrement(x0, x1, count)
                        if (increment.isFinite()) {
                            if (increment > 0) {
                                x1 = (floor(x1 / increment) + 1) * increment
                            } else if (increment < 0) {
                                x1 = (ceil(x1 * -increment) + 1) / -increment
                            }
                        }
                    } else {
                        tz.removeAt(tz.size - 1)
                    }
                }
                tz
            }
        }

        var a = 0
        var b = computed.size
        while (a < computed.size && computed[a] <= x0) a++
        while (b > 0 && computed[b - 1] > x1) b--
        val thresholds = if (a != 0 || b < computed.size) computed.subList(a, b) else computed
        val m = thresholds.size

        val binValues = List(m + 1) { ArrayList<T>() }
        when {
            step.isFinite() && step > 0 -> for (i in 0 until n) {
                val x = values[i]
                if (!x.isNaN() && x in x0..x1) {
                    binValues[min(m, floor((x - x0) / step).toInt())].add(dataList[i])
                }
            }
            step.isFinite() && step < 0 -> for (i in 0 until n) {
                val x = values[i]
                if (!x.isNaN() && x in x0..x1) {
                    val j = floor((x0 - x) * step).toInt()
                    val offset = if (j < thresholds.size && thresholds[j] <= x) 1 else 0
                    binValues[min(m, j + offset)].add(dataList[i])
                }
            }
            else -> for (i in 0 until n) {
                val x = values[i]
                if (!x.isNaN() && x in x0..x1) {
                    binValues[thresholds.bisect(x, 0, m)].add(dataList[i])
                }
            }
        }

        return List(m + 1) { i ->
            val binX0 = if (i > 0) thresholds[i - 1] else x0
            val binX1 = if (i < m) thresholds[i] else x1
            Bin(binX0, binX1, binValues[i])
        }
    }
}

/** Creates a [Binner] reading numeric values via [value]. See [d3.bin](https://github.com/d3/d3-array#bin). */
public fun <T> bin(value: (T) -> Double): Binner<T> =
    Binner(value, null, ThresholdSpec.Count(::thresholdSturges))

/** Alias for [bin]. See [d3.histogram](https://github.com/d3/d3-array#bin). */
public fun <T> histogram(value: (T) -> Double): Binner<T> = bin(value)

/** See equivalent [d3 function](https://github.com/d3/d3-array#thresholdSturges). */
public fun thresholdSturges(values: List<Double>, min: Double, max: Double): Int {
    val count = values.count { it }
    if (count == 0) return 1
    return max(1.0, ceil(ln(count.toDouble()) / ln(2.0)) + 1.0).toInt()
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#thresholdScott). */
public fun thresholdScott(values: List<Double>, min: Double, max: Double): Int {
    val count = values.count { it }
    val deviation = values.deviation { it }
    return if (count > 0 && deviation != null && deviation != 0.0) {
        ceil((max - min) * cbrt(count.toDouble()) / (3.49 * deviation)).toInt()
    } else {
        1
    }
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#thresholdFreedmanDiaconis). */
public fun thresholdFreedmanDiaconis(values: List<Double>, min: Double, max: Double): Int {
    val count = values.count { it }
    val q3 = values.quantile(0.75) { it }
    val q1 = values.quantile(0.25) { it }
    val d = if (q3 != null && q1 != null) q3 - q1 else null
    return if (count > 0 && d != null && d != 0.0) {
        ceil((max - min) / (2 * d * count.toDouble().pow(-1.0 / 3.0))).toInt()
    } else {
        1
    }
}

private fun extent(values: List<Double>): Pair<Double, Double> {
    var min = Double.NaN
    var max = Double.NaN
    for (value in values) {
        if (value.isNaN()) continue
        if (min.isNaN() || value < min) min = value
        if (max.isNaN() || value > max) max = value
    }
    return min to max
}
