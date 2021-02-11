package com.juul.krayon.chart.data

/** A non-empty [List]. A cluster is usually used to represent multiple nouns within a single state or category. */
public data class Cluster<out T>(
    private val entries: List<T>,
) : List<T> by entries {
    init {
        require(entries.isNotEmpty()) { "A cluster must have at least 1 entry." }
    }
}

public fun <T> clusterOf(firstValue: T, vararg moreValues: T): Cluster<T> =
    Cluster(listOf(firstValue, *moreValues))

public fun <T> Iterable<T>.toCluster(): Cluster<T> =
    Cluster(toList())
