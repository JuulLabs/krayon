package com.juul.krayon.scale

/**
 * Returns the insertion index for [x] in the sorted (ascending) sublist `[lo, hi)`, to the right of any equal elements.
 * Equivalent to [d3-array's bisectRight](https://github.com/d3/d3-array#bisectRight).
 */
internal fun <T : Comparable<T>> bisectRight(values: List<T>, x: T, lo: Int = 0, hi: Int = values.size): Int {
    var low = lo
    var high = hi
    while (low < high) {
        val mid = (low + high) ushr 1
        if (x < values[mid]) high = mid else low = mid + 1
    }
    return low
}
