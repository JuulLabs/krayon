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
 * A histogram bin, behaving as the [List] of values it contains, bounded by [lowerBound]
 * (inclusive) and [upperBound] (inclusive only for the final bin, otherwise exclusive).
 */
public class Bin<out T> internal constructor(
    public val lowerBound: Double,
    public val upperBound: Double,
    values: List<T>,
) : List<T> by values {

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is Bin<*> && lowerBound == other.lowerBound && upperBound == other.upperBound && toList() == other.toList())

    override fun hashCode(): Int = (lowerBound.hashCode() * 31 + upperBound.hashCode()) * 31 + toList().hashCode()

    override fun toString(): String = "Bin($lowerBound..$upperBound, ${toList()})"
}

/** Strategy for choosing histogram bin boundaries. See [bin]. */
public sealed interface Thresholds {

    /** [Sturges' formula](https://en.wikipedia.org/wiki/Histogram#Sturges'_formula); the default. */
    public data object Sturges : Thresholds

    /** [Scott's normal reference rule](https://en.wikipedia.org/wiki/Histogram#Scott's_normal_reference_rule). */
    public data object Scott : Thresholds

    /** The [Freedman–Diaconis rule](https://en.wikipedia.org/wiki/Freedman%E2%80%93Diaconis_rule). */
    public data object FreedmanDiaconis : Thresholds

    /** An approximate number of bins; the exact count is adjusted to produce human-friendly boundaries. */
    public data class Count(val count: Int) : Thresholds

    /** Explicit interior boundary values. */
    public data class Boundaries(val values: List<Double>) : Thresholds
}

/**
 * Groups values into consecutive, non-overlapping [Bin]s. Mirrors
 * [d3.bin](https://github.com/d3/d3-array#bins).
 *
 * @param domain restricts and scales binning to `[start, endInclusive]`; defaults to the extent of
 *   the mapped values, in which case the bounds are rounded to human-friendly values.
 * @param thresholds the [Thresholds] strategy for choosing bin boundaries.
 * @param value maps each element to the number that determines its bin.
 */
public fun <T> Iterable<T>.bin(
    domain: ((values: List<Double>) -> ClosedFloatingPointRange<Double>)? = null,
    thresholds: Thresholds = Thresholds.Sturges,
    value: (T) -> Double,
): List<Bin<T>> {
    val elements = toList()
    val values = elements.map(value)

    val usingDefaultDomain = domain == null
    val extent = domain?.invoke(values) ?: values.extent()
    var lowerBound = extent.start
    var upperBound = extent.endInclusive
    var uniformStep = Double.NaN

    val boundaries: List<Double> = if (thresholds is Thresholds.Boundaries) {
        thresholds.values
    } else {
        val count = thresholds.binCount(values, lowerBound, upperBound)
        val maxValue = upperBound
        if (usingDefaultDomain) {
            val (niceLower, niceUpper) = nice(lowerBound, upperBound, count)
            lowerBound = niceLower
            upperBound = niceUpper
        }
        val ticks = ticks(lowerBound, upperBound, count).toMutableList()
        if (ticks.firstOrNull()?.let { it <= lowerBound } == true) {
            uniformStep = tickIncrement(lowerBound, upperBound, count)
        }
        if (ticks.lastOrNull()?.let { it >= upperBound } == true) {
            if (maxValue >= upperBound && usingDefaultDomain) {
                val increment = tickIncrement(lowerBound, upperBound, count)
                if (increment.isFinite()) {
                    upperBound = when {
                        increment > 0 -> (floor(upperBound / increment) + 1) * increment
                        increment < 0 -> (ceil(upperBound * -increment) + 1) / -increment
                        else -> upperBound
                    }
                }
            } else {
                ticks.removeAt(ticks.lastIndex)
            }
        }
        ticks
    }

    val interior = boundaries.trimmedTo(lowerBound, upperBound)
    val binCount = interior.size + 1
    val contents = List(binCount) { mutableListOf<T>() }

    for ((index, x) in values.withIndex()) {
        if (x.isNaN() || x < lowerBound || x > upperBound) continue
        val bin = when {
            uniformStep > 0 -> min(interior.size, floor((x - lowerBound) / uniformStep).toInt())
            uniformStep < 0 -> {
                val approximate = floor((lowerBound - x) * uniformStep).toInt()
                val correction = if (approximate < interior.size && interior[approximate] <= x) 1 else 0
                min(interior.size, approximate + correction)
            }
            else -> interior.bisect(x)
        }
        contents[bin].add(elements[index])
    }

    return List(binCount) { i ->
        Bin(
            lowerBound = if (i > 0) interior[i - 1] else lowerBound,
            upperBound = if (i < interior.size) interior[i] else upperBound,
            values = contents[i],
        )
    }
}

private fun List<Double>.trimmedTo(lowerBound: Double, upperBound: Double): List<Double> {
    var start = 0
    var end = size
    while (start < size && this[start] <= lowerBound) start++
    while (end > 0 && this[end - 1] > upperBound) end--
    return if (start != 0 || end < size) subList(start, end) else this
}

private fun Thresholds.binCount(values: List<Double>, lowerBound: Double, upperBound: Double): Int =
    when (this) {
        is Thresholds.Count -> count
        Thresholds.Sturges -> sturgesBinCount(values)
        Thresholds.Scott -> scottBinCount(values, lowerBound, upperBound)
        Thresholds.FreedmanDiaconis -> freedmanDiaconisBinCount(values, lowerBound, upperBound)
        is Thresholds.Boundaries -> error("Boundaries thresholds do not have a bin count")
    }

private fun sturgesBinCount(values: List<Double>): Int {
    val count = values.count { !it.isNaN() }
    if (count == 0) return 1
    return max(1.0, ceil(ln(count.toDouble()) / ln(2.0)) + 1.0).toInt()
}

private fun scottBinCount(values: List<Double>, lowerBound: Double, upperBound: Double): Int {
    val count = values.count { !it.isNaN() }
    val deviation = values.standardDeviation()
    return if (count > 0 && deviation != null && deviation != 0.0) {
        ceil((upperBound - lowerBound) * cbrt(count.toDouble()) / (3.49 * deviation)).toInt()
    } else {
        1
    }
}

private fun freedmanDiaconisBinCount(values: List<Double>, lowerBound: Double, upperBound: Double): Int {
    val count = values.count { !it.isNaN() }
    val thirdQuartile = values.quantile(0.75)
    val firstQuartile = values.quantile(0.25)
    val interquartileRange = if (thirdQuartile != null && firstQuartile != null) thirdQuartile - firstQuartile else null
    return if (count > 0 && interquartileRange != null && interquartileRange != 0.0) {
        ceil((upperBound - lowerBound) / (2 * interquartileRange * count.toDouble().pow(-1.0 / 3.0))).toInt()
    } else {
        1
    }
}

private fun List<Double>.extent(): ClosedFloatingPointRange<Double> {
    var min = Double.NaN
    var max = Double.NaN
    for (value in this) {
        if (value.isNaN()) continue
        if (min.isNaN() || value < min) min = value
        if (max.isNaN() || value > max) max = value
    }
    return if (min.isNaN()) 0.0..0.0 else min..max
}
