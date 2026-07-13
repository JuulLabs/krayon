package com.juul.krayon.collections

/** See equivalent [d3 function](https://github.com/d3/d3-array#ascending). */
public fun <T : Comparable<T>> ascending(a: T, b: T): Int = a.compareTo(b)

/** See equivalent [d3 function](https://github.com/d3/d3-array#descending). */
public fun <T : Comparable<T>> descending(a: T, b: T): Int = b.compareTo(a)

/** A [Comparator] ordering elements ascending by the value of [selector]; nulls sort first. */
public fun <T, R : Comparable<R>> ascendingKey(selector: (T) -> R?): Comparator<T> =
    Comparator { a, b -> compareValues(selector(a), selector(b)) }

/** A [Comparator] ordering elements descending by the value of [selector]; nulls sort last. */
public fun <T, R : Comparable<R>> descendingKey(selector: (T) -> R?): Comparator<T> =
    Comparator { a, b -> compareValues(selector(b), selector(a)) }

/** See equivalent [d3 function](https://github.com/d3/d3-array#greatest). */
public fun <T> Iterable<T>.greatest(comparator: Comparator<in T>): T? = maxWithOrNull(comparator)

/** See equivalent [d3 function](https://github.com/d3/d3-array#greatest). */
public fun <T, R : Comparable<R>> Iterable<T>.greatest(selector: (T) -> R): T? = maxByOrNull(selector)

/** See equivalent [d3 function](https://github.com/d3/d3-array#least). */
public fun <T> Iterable<T>.least(comparator: Comparator<in T>): T? = minWithOrNull(comparator)

/** See equivalent [d3 function](https://github.com/d3/d3-array#least). */
public fun <T, R : Comparable<R>> Iterable<T>.least(selector: (T) -> R): T? = minByOrNull(selector)

/** See equivalent [d3 function](https://github.com/d3/d3-array#greatestIndex). Returns `-1` if empty. */
public fun <T> Iterable<T>.greatestIndex(comparator: Comparator<in T>): Int {
    var maxIndex = -1
    var maxValue: T? = null
    var index = -1
    for (value in this) {
        index++
        @Suppress("UNCHECKED_CAST")
        if (maxIndex < 0 || comparator.compare(value, maxValue as T) > 0) {
            maxValue = value
            maxIndex = index
        }
    }
    return maxIndex
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#greatestIndex). Returns `-1` if empty. */
public fun <T, R : Comparable<R>> Iterable<T>.greatestIndex(selector: (T) -> R): Int {
    var maxIndex = -1
    var maxValue: R? = null
    var index = -1
    for (value in this) {
        index++
        val current = selector(value)
        if (maxIndex < 0 || current > maxValue!!) {
            maxValue = current
            maxIndex = index
        }
    }
    return maxIndex
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#leastIndex). Returns `-1` if empty. */
public fun <T> Iterable<T>.leastIndex(comparator: Comparator<in T>): Int {
    var minIndex = -1
    var minValue: T? = null
    var index = -1
    for (value in this) {
        index++
        @Suppress("UNCHECKED_CAST")
        if (minIndex < 0 || comparator.compare(value, minValue as T) < 0) {
            minValue = value
            minIndex = index
        }
    }
    return minIndex
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#leastIndex). Returns `-1` if empty. */
public fun <T, R : Comparable<R>> Iterable<T>.leastIndex(selector: (T) -> R): Int {
    var minIndex = -1
    var minValue: R? = null
    var index = -1
    for (value in this) {
        index++
        val current = selector(value)
        if (minIndex < 0 || current < minValue!!) {
            minValue = current
            minIndex = index
        }
    }
    return minIndex
}
