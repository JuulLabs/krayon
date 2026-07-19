package com.juul.krayon.collections

/**
 * The leftmost index in `[from, to)` at which [value] could be inserted to keep the ascending-sorted
 * receiver ordered; all elements before it are strictly less than [value].
 */
public fun <T : Comparable<T>> List<T>.bisectLeft(value: T, from: Int = 0, to: Int = size): Int {
    var low = from
    var high = to
    while (low < high) {
        val mid = (low + high) ushr 1
        if (this[mid] < value) low = mid + 1 else high = mid
    }
    return low
}

/**
 * The rightmost index in `[from, to)` at which [value] could be inserted to keep the ascending-sorted
 * receiver ordered; all elements before it are less than or equal to [value].
 */
public fun <T : Comparable<T>> List<T>.bisectRight(value: T, from: Int = 0, to: Int = size): Int {
    var low = from
    var high = to
    while (low < high) {
        val mid = (low + high) ushr 1
        if (this[mid] <= value) low = mid + 1 else high = mid
    }
    return low
}

/** Alias for [bisectRight]. */
public fun <T : Comparable<T>> List<T>.bisect(value: T, from: Int = 0, to: Int = size): Int =
    bisectRight(value, from, to)

/** As [bisectLeft], comparing [key] against the value of [selector] for each element. */
public fun <T, K : Comparable<K>> List<T>.bisectLeft(key: K, from: Int = 0, to: Int = size, selector: (T) -> K): Int {
    var low = from
    var high = to
    while (low < high) {
        val mid = (low + high) ushr 1
        if (selector(this[mid]) < key) low = mid + 1 else high = mid
    }
    return low
}

/** As [bisectRight], comparing [key] against the value of [selector] for each element. */
public fun <T, K : Comparable<K>> List<T>.bisectRight(key: K, from: Int = 0, to: Int = size, selector: (T) -> K): Int {
    var low = from
    var high = to
    while (low < high) {
        val mid = (low + high) ushr 1
        if (selector(this[mid]) <= key) low = mid + 1 else high = mid
    }
    return low
}
