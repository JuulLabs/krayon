package com.juul.krayon.collections

/**
 * The zero-based rank of each element in iteration order, ordering elements with [comparator].
 * Tied elements all receive the smallest rank of their group.
 */
public fun <T> Iterable<T>.rank(comparator: Comparator<in T>): IntArray {
    val elements = toList()
    val orderedIndices = elements.indices.sortedWith { a, b -> comparator.compare(elements[a], elements[b]) }
    val ranks = IntArray(elements.size)
    orderedIndices.forEachIndexed { position, index ->
        val previousIndex = orderedIndices.getOrNull(position - 1)
        ranks[index] = if (previousIndex != null && comparator.compare(elements[previousIndex], elements[index]) == 0) {
            ranks[previousIndex]
        } else {
            position
        }
    }
    return ranks
}

/** The zero-based rank of each element by the value of [selector], with ties sharing the smallest rank. */
public fun <T, R : Comparable<R>> Iterable<T>.rank(selector: (T) -> R): IntArray =
    rank(compareBy(selector))
