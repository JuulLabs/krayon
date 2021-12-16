package com.juul.krayon.chart.data

/** A collection of data, grouped into [Series]. */
@Deprecated("Chart module removed in favor of D3-like paradigm.")
public interface DataSet<out T> {
    /** The data. Guaranteed to have at least one series. */
    public val seriesData: List<Series<T>>
}

@Deprecated("Chart module removed in favor of D3-like paradigm.")
private class SimpleDataSet<T>(
    override val seriesData: List<Series<T>>,
) : DataSet<T> {
    init {
        require(seriesData.isNotEmpty()) { "Must have at least one series." }
    }
}

/** Creates a [DataSet] from one or more [Series]. */
@Deprecated("Chart module removed in favor of D3-like paradigm.")
public fun <T> dataSetOf(firstSeries: Series<T>, vararg moreSeries: Series<T>): DataSet<T> =
    SimpleDataSet(listOf(firstSeries, *moreSeries))

/** Creates a [DataSet] with a single [Series]. */
@Deprecated("Chart module removed in favor of D3-like paradigm.")
public fun <T> Series<T>.toDataSet(): DataSet<T> =
    SimpleDataSet(listOf(this))
