package com.juul.krayon.chart.data

import com.juul.krayon.chart.checkRectangular
import kotlin.jvm.JvmName

/** A collection of data which can be grouped into [Cluster]s. */
@Deprecated("Chart module removed in favor of D3-like paradigm.")
public interface ClusteredDataSet<out T> : DataSet<T> {
    /** The clustered data. Guaranteed to have at least one cluster. */
    public val clusterData: List<Cluster<T>>
}

@Deprecated("Chart module removed in favor of D3-like paradigm.")
internal class RectangularClusteredDataSet<T> private constructor(
    override val seriesData: List<Series<T>>,
    override val clusterData: List<Cluster<T>>,
) : ClusteredDataSet<T> {

    init {
        // Rectangularity is checked by the constructor functions, no need to do it here.
        require(seriesData.isNotEmpty() && clusterData.isNotEmpty()) { "Must have at least one series/cluster." }
    }

    companion object {
        fun <T> fromSeriesData(seriesData: List<Series<T>>) = RectangularClusteredDataSet(
            seriesData,
            seriesData.transposeOf().map { Cluster(it) }
        )

        fun <T> fromClusterData(clusterData: List<Cluster<T>>) = RectangularClusteredDataSet(
            clusterData.transposeOf().map { Series(it) },
            clusterData
        )

        @Suppress("UnnecessaryVariable") // I'm gonna make some extra variables in hopes that it makes understanding easier
        fun <T> List<List<T>>.transposeOf(): List<List<T>> {
            if (isEmpty()) return emptyList()
            checkRectangular(this)
            val newOuterLength = first().size
            return (0 until newOuterLength).map { newOuterIndex ->
                val oldOuterLength = size
                (0 until oldOuterLength).map { oldOuterIndex ->
                    val oldInnerIndex = newOuterIndex
                    this[oldOuterIndex][oldInnerIndex]
                }
            }
        }
    }
}

/** Creates [ClusteredDataSet] with a single [Series]. */
@Deprecated("Chart module removed in favor of D3-like paradigm.")
public fun <T> Series<T>.toClusteredDataSet(): ClusteredDataSet<T> =
    RectangularClusteredDataSet.fromSeriesData(listOf(this))

/** Creates a [ClusteredDataSet] from one or more [Series]. Each series must have the same length. */
@Deprecated("Chart module removed in favor of D3-like paradigm.")
public fun <T> rectangularDataSetOf(firstSeries: Series<T>, vararg moreSeries: Series<T>): ClusteredDataSet<T> =
    RectangularClusteredDataSet.fromSeriesData(listOf(firstSeries, *moreSeries))

/** Creates [ClusteredDataSet] with a list of [Series]s. Each series must have the same length */
@Deprecated("Chart module removed in favor of D3-like paradigm.")
@JvmName("seriesToRectangularDataSet")
public fun <T> Iterable<Series<T>>.toRectangularDataSet(): ClusteredDataSet<T> =
    RectangularClusteredDataSet.fromSeriesData(this.toList())

/** Creates [ClusteredDataSet] with a single [Cluster]. */
@Deprecated("Chart module removed in favor of D3-like paradigm.")
public fun <T> Cluster<T>.toClusteredDataSet(): ClusteredDataSet<T> =
    RectangularClusteredDataSet.fromClusterData(listOf(this))

/** Creates a [ClusteredDataSet] from one or more [Cluster]s. Each cluster must have the same length. */
@Deprecated("Chart module removed in favor of D3-like paradigm.")
public fun <T> rectangularDataSetOf(firstCluster: Cluster<T>, vararg moreClusters: Cluster<T>): ClusteredDataSet<T> =
    RectangularClusteredDataSet.fromClusterData(listOf(firstCluster, *moreClusters))

/** Creates [ClusteredDataSet] with a list of [Cluster]s. Each cluster must have the same length */
@Deprecated("Chart module removed in favor of D3-like paradigm.")
@JvmName("clustersToRectangularDataSet")
public fun <T> Iterable<Cluster<T>>.toRectangularDataSet(): ClusteredDataSet<T> =
    RectangularClusteredDataSet.fromClusterData(this.toList())
