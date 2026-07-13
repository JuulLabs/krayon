package com.juul.krayon.collections

/** See equivalent [d3 function](https://github.com/d3/d3-array#group). */
public fun <T, K> Iterable<T>.group(key: (T) -> K): Map<K, List<T>> =
    groupBy(key)

/** See equivalent [d3 function](https://github.com/d3/d3-array#group). */
public fun <T, K1, K2> Iterable<T>.group(
    key1: (T) -> K1,
    key2: (T) -> K2,
): Map<K1, Map<K2, List<T>>> =
    groupBy(key1).mapValues { (_, group) -> group.groupBy(key2) }

/** See equivalent [d3 function](https://github.com/d3/d3-array#groups). */
public fun <T, K> Iterable<T>.groups(key: (T) -> K): List<Pair<K, List<T>>> =
    group(key).toList()

/** See equivalent [d3 function](https://github.com/d3/d3-array#index). */
public fun <T, K> Iterable<T>.index(key: (T) -> K): Map<K, T> {
    val result = LinkedHashMap<K, T>()
    for (element in this) {
        val k = key(element)
        require(k !in result) { "Duplicate key: $k" }
        result[k] = element
    }
    return result
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#indexes). */
public fun <T, K> Iterable<T>.indexes(key: (T) -> K): List<Pair<K, T>> =
    index(key).toList()

/** See equivalent [d3 function](https://github.com/d3/d3-array#rollup). */
public fun <T, K, R> Iterable<T>.rollup(key: (T) -> K, reduce: (List<T>) -> R): Map<K, R> =
    group(key).mapValues { (_, group) -> reduce(group) }

/** See equivalent [d3 function](https://github.com/d3/d3-array#rollup). */
public fun <T, K1, K2, R> Iterable<T>.rollup(
    key1: (T) -> K1,
    key2: (T) -> K2,
    reduce: (List<T>) -> R,
): Map<K1, Map<K2, R>> =
    group(key1).mapValues { (_, group) ->
        group.groupBy(key2).mapValues { (_, subgroup) -> reduce(subgroup) }
    }

/** See equivalent [d3 function](https://github.com/d3/d3-array#rollups). */
public fun <T, K, R> Iterable<T>.rollups(key: (T) -> K, reduce: (List<T>) -> R): List<Pair<K, R>> =
    rollup(key, reduce).toList()

/** See equivalent [d3 function](https://github.com/d3/d3-array#flatGroup). */
public fun <T, K> Iterable<T>.flatGroup(key: (T) -> K): List<Pair<K, List<T>>> =
    groups(key)

/** See equivalent [d3 function](https://github.com/d3/d3-array#flatGroup). */
public fun <T, K1, K2> Iterable<T>.flatGroup(
    key1: (T) -> K1,
    key2: (T) -> K2,
): List<Triple<K1, K2, List<T>>> {
    val result = ArrayList<Triple<K1, K2, List<T>>>()
    for ((k1, group) in group(key1)) {
        for ((k2, subgroup) in group.groupBy(key2)) {
            result.add(Triple(k1, k2, subgroup))
        }
    }
    return result
}

/** See equivalent [d3 function](https://github.com/d3/d3-array#flatRollup). */
public fun <T, K, R> Iterable<T>.flatRollup(key: (T) -> K, reduce: (List<T>) -> R): List<Pair<K, R>> =
    rollups(key, reduce)

/** See equivalent [d3 function](https://github.com/d3/d3-array#flatRollup). */
public fun <T, K1, K2, R> Iterable<T>.flatRollup(
    key1: (T) -> K1,
    key2: (T) -> K2,
    reduce: (List<T>) -> R,
): List<Triple<K1, K2, R>> {
    val result = ArrayList<Triple<K1, K2, R>>()
    for ((k1, group) in group(key1)) {
        for ((k2, subgroup) in group.groupBy(key2)) {
            result.add(Triple(k1, k2, reduce(subgroup)))
        }
    }
    return result
}
