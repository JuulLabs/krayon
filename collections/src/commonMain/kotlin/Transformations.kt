package com.juul.krayon.collections

import kotlin.random.Random

/** See equivalent [d3 function](https://github.com/d3/d3-array#cross). */
public fun <A, B> cross(a: Iterable<A>, b: Iterable<B>): List<Pair<A, B>> =
    cross(a, b) { first, second -> first to second }

/** See equivalent [d3 function](https://github.com/d3/d3-array#cross). */
public inline fun <A, B, R> cross(a: Iterable<A>, b: Iterable<B>, reduce: (A, B) -> R): List<R> {
    val result = ArrayList<R>()
    for (first in a) {
        for (second in b) {
            result.add(reduce(first, second))
        }
    }
    return result
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#merge). */
public fun <T> merge(arrays: Iterable<Iterable<T>>): List<T> {
    val result = ArrayList<T>()
    for (array in arrays) result.addAll(array)
    return result
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#pairs). */
public fun <T> Iterable<T>.pairs(): List<Pair<T, T>> =
    pairs { a, b -> a to b }

/** See equivalent [d3 function](https://github.com/d3/d3-array#pairs). */
public inline fun <T, R> Iterable<T>.pairs(transform: (T, T) -> R): List<R> {
    val result = ArrayList<R>()
    val iterator = iterator()
    if (!iterator.hasNext()) return result
    var previous = iterator.next()
    while (iterator.hasNext()) {
        val current = iterator.next()
        result.add(transform(previous, current))
        previous = current
    }
    return result
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#permute). */
public fun <T> permute(source: List<T>, keys: Iterable<Int>): List<T> =
    keys.map { source[it] }

/** Reorders the values of [source] into the order of [keys]. See equivalent [d3 function](https://github.com/d3/d3-array#permute). */
public fun <K, V> permute(source: Map<K, V>, keys: Iterable<K>): List<V> =
    keys.map { key -> source.getValue(key) }

/** See equivalent [d3 function](https://github.com/d3/d3-array#transpose). */
public fun <T> transpose(matrix: List<List<T>>): List<List<T>> {
    if (matrix.isEmpty()) return emptyList()
    val columns = matrix.minOf { it.size }
    return List(columns) { i -> List(matrix.size) { j -> matrix[j][i] } }
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#zip). */
public fun <T> zip(vararg arrays: List<T>): List<List<T>> =
    transpose(arrays.asList())

/** See equivalent [d3 function](https://github.com/d3/d3-array#shuffle). */
public fun <T> shuffle(list: MutableList<T>, from: Int = 0, to: Int = list.size, random: Random = Random.Default): MutableList<T> =
    Shuffler(random).shuffle(list, from, to)

/** See equivalent [d3 function](https://github.com/d3/d3-array#shuffler). */
public fun shuffler(random: Random): Shuffler = Shuffler(random)

/** A Fisher–Yates shuffler bound to a specific [random] source. */
public class Shuffler internal constructor(private val random: Random) {

    /** Shuffles the range `[from, to)` of [list] in place, returning the same [list]. */
    public fun <T> shuffle(list: MutableList<T>, from: Int = 0, to: Int = list.size): MutableList<T> {
        var m = to - from
        while (m > 0) {
            val i = random.nextInt(m)
            m--
            val t = list[m + from]
            list[m + from] = list[i + from]
            list[i + from] = t
        }
        return list
    }
}
