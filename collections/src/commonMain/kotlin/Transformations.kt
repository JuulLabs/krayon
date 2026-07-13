package com.juul.krayon.collections

/** The cartesian product of the receiver with [other], pairing every element of each. */
public infix fun <A, B> Iterable<A>.cross(other: Iterable<B>): List<Pair<A, B>> =
    flatMap { first -> other.map { second -> first to second } }

/**
 * The transpose of a matrix (list of rows), swapping rows and columns. The result has one row per
 * column of the shortest input row.
 */
public fun <T> List<List<T>>.transpose(): List<List<T>> {
    if (isEmpty()) return emptyList()
    val columnCount = minOf { it.size }
    return List(columnCount) { column -> List(size) { row -> this[row][column] } }
}
