package com.juul.krayon.shape

/** Determines the order in which series are stacked, returning the series indices in stacking order. */
public fun interface StackOrder {
    public operator fun invoke(series: List<Series<*, *>>): IntArray
}

/** Series are stacked in the given (input) order. */
public object StackOrderNone : StackOrder {
    override fun invoke(series: List<Series<*, *>>): IntArray = IntArray(series.size) { it }
}

/** Series are stacked in reverse of the given order. */
public object StackOrderReverse : StackOrder {
    override fun invoke(series: List<Series<*, *>>): IntArray = IntArray(series.size) { series.size - 1 - it }
}

/** Series are stacked by ascending total value (smallest at the bottom). */
public object StackOrderAscending : StackOrder {
    override fun invoke(series: List<Series<*, *>>): IntArray {
        val sums = series.map(::seriesSum)
        return series.indices.sortedBy { sums[it] }.toIntArray()
    }
}

/** Series are stacked by descending total value (largest at the bottom). */
public object StackOrderDescending : StackOrder {
    override fun invoke(series: List<Series<*, *>>): IntArray = StackOrderAscending(series).reversedArray()
}

/** Series are stacked by the index of their maximum value (order of appearance). */
public object StackOrderAppearance : StackOrder {
    override fun invoke(series: List<Series<*, *>>): IntArray {
        val peaks = series.map(::seriesPeak)
        return series.indices.sortedBy { peaks[it] }.toIntArray()
    }
}

/** Series are stacked inside-out by appearance, so that larger series are toward the center. */
public object StackOrderInsideOut : StackOrder {
    override fun invoke(series: List<Series<*, *>>): IntArray {
        val n = series.size
        val sums = series.map(::seriesSum)
        val order = StackOrderAppearance(series)
        var top = 0f
        var bottom = 0f
        val tops = mutableListOf<Int>()
        val bottoms = mutableListOf<Int>()
        for (i in 0 until n) {
            val j = order[i]
            if (top < bottom) {
                top += sums[j]
                tops += j
            } else {
                bottom += sums[j]
                bottoms += j
            }
        }
        return (bottoms.asReversed() + tops).toIntArray()
    }
}

internal fun seriesSum(series: Series<*, *>): Float {
    var sum = 0f
    for (point in series) {
        val value = point.high
        if (!value.isNaN() && value != 0f) sum += value
    }
    return sum
}

internal fun seriesPeak(series: Series<*, *>): Int {
    var peak = 0
    var max = Float.NEGATIVE_INFINITY
    for (i in series.indices) {
        val value = series[i].high
        if (value > max) {
            max = value
            peak = i
        }
    }
    return peak
}
