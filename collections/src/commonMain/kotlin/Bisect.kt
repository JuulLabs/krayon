package com.juul.krayon.collections

/**
 * Binary search helper producing insertion points into a sorted list. See equivalent
 * [d3 function](https://github.com/d3/d3-array#bisector).
 */
public class Bisector<T, V> internal constructor(
    private val compare: (T, V) -> Int,
) {

    /**
     * Returns the leftmost insertion index for [x] within `[from, to)` of the sorted [list],
     * such that all elements to the left compare strictly less than [x].
     */
    public fun left(list: List<T>, x: V, from: Int = 0, to: Int = list.size): Int {
        var lo = from
        var hi = to
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (compare(list[mid], x) < 0) lo = mid + 1 else hi = mid
        }
        return lo
    }

    /**
     * Returns the rightmost insertion index for [x] within `[from, to)` of the sorted [list],
     * such that all elements to the left compare less than or equal to [x].
     */
    public fun right(list: List<T>, x: V, from: Int = 0, to: Int = list.size): Int {
        var lo = from
        var hi = to
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (compare(list[mid], x) <= 0) lo = mid + 1 else hi = mid
        }
        return lo
    }
}

/** Creates a [Bisector] that locates a value by comparing it against the value of [accessor]. */
public fun <T, V : Comparable<V>> bisector(accessor: (T) -> V): Bisector<T, V> =
    Bisector(compare = { element, x -> accessor(element).compareTo(x) })

/** Creates a [Bisector] that locates an element using the given [comparator]. */
public fun <T> bisector(comparator: Comparator<in T>): Bisector<T, T> =
    Bisector(compare = { element, x -> comparator.compare(element, x) })

/** See equivalent [d3 function](https://github.com/d3/d3-array#bisectLeft). */
public fun <T : Comparable<T>> List<T>.bisectLeft(x: T, from: Int = 0, to: Int = size): Int {
    var lo = from
    var hi = to
    while (lo < hi) {
        val mid = (lo + hi) ushr 1
        if (this[mid] < x) lo = mid + 1 else hi = mid
    }
    return lo
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#bisectRight). */
public fun <T : Comparable<T>> List<T>.bisectRight(x: T, from: Int = 0, to: Int = size): Int {
    var lo = from
    var hi = to
    while (lo < hi) {
        val mid = (lo + hi) ushr 1
        if (this[mid] <= x) lo = mid + 1 else hi = mid
    }
    return lo
}

/** Alias for [bisectRight]. See equivalent [d3 function](https://github.com/d3/d3-array#bisect). */
public fun <T : Comparable<T>> List<T>.bisect(x: T, from: Int = 0, to: Int = size): Int =
    bisectRight(x, from, to)
