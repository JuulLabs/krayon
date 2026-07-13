package com.juul.krayon.collections

/**
 * Returns the 0-based rank of each element, where ties share the smallest rank of the group.
 * See equivalent [d3 function](https://github.com/d3/d3-array#rank).
 */
public fun <T> Iterable<T>.rank(comparator: Comparator<in T>): IntArray {
    val values = toList()
    val n = values.size
    val order = (0 until n).sortedWith { i, j -> comparator.compare(values[i], values[j]) }
    val ranks = IntArray(n)
    var previous: T? = null
    var previousDefined = false
    var rank = 0
    for ((position, index) in order.withIndex()) {
        @Suppress("UNCHECKED_CAST")
        val comparison = if (previousDefined) comparator.compare(values[index], previous as T) else 0
        if (!previousDefined || comparison > 0) {
            previous = values[index]
            previousDefined = true
            rank = position
        }
        ranks[index] = rank
    }
    return ranks
}

/**
 * Returns the 0-based rank of each element by the value produced by [selector], where ties share
 * the smallest rank of the group. See equivalent [d3 function](https://github.com/d3/d3-array#rank).
 */
public fun <T, R : Comparable<R>> Iterable<T>.rank(selector: (T) -> R): IntArray =
    rank(compareBy(selector))
