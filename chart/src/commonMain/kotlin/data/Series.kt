package com.juul.krayon.chart.data

/** A non-empty [List]. A series is usually used to represent multiple states or subsets of a single noun. */
public data class Series<out T>(
    private val entries: List<T>
) : List<T> by entries {
    init {
        require(entries.isNotEmpty()) { "A series must have at least 1 entry." }
    }
}

public fun <T> seriesOf(firstValue: T, vararg moreValues: T): Series<T> =
    Series(listOf(firstValue, *moreValues))

public fun <T> Iterable<T>.toSeries(): Series<T> =
    Series(toList())
