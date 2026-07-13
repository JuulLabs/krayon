package com.juul.krayon.collections

/** See equivalent [d3 function](https://github.com/d3/d3-array#union). */
public fun <T> union(vararg iterables: Iterable<T>): Set<T> {
    val result = LinkedHashSet<T>()
    for (iterable in iterables) result.addAll(iterable)
    return result
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#intersection). */
public fun <T> intersection(vararg iterables: Iterable<T>): Set<T> {
    if (iterables.isEmpty()) return emptySet()
    val result = LinkedHashSet(iterables[0].toList())
    for (i in 1 until iterables.size) {
        val other = iterables[i].toHashSet()
        result.retainAll(other)
    }
    return result
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#difference). */
public fun <T> difference(values: Iterable<T>, vararg others: Iterable<T>): Set<T> {
    val result = LinkedHashSet(values.toList())
    for (other in others) {
        for (value in other) result.remove(value)
    }
    return result
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#superset). Returns true if [values] contains every element of [other]. */
public fun <T> superset(values: Iterable<T>, other: Iterable<T>): Boolean {
    val set = values.toHashSet()
    return other.all { it in set }
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#subset). Returns true if every element of [values] is in [other]. */
public fun <T> subset(values: Iterable<T>, other: Iterable<T>): Boolean =
    superset(other, values)

/** See equivalent [d3 function](https://github.com/d3/d3-array#disjoint). Returns true if [values] and [other] share no elements. */
public fun <T> disjoint(values: Iterable<T>, other: Iterable<T>): Boolean {
    val set = values.toHashSet()
    return other.none { it in set }
}
