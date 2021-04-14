package com.juul.krayon.sample

import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.data.toCluster
import com.juul.krayon.chart.data.toRectangularDataSet
import kotlin.random.Random

internal fun getRandomData(): ClusteredDataSet<Float> {
    val numClusters = Random.nextInt(1, 5)
    val numSeries = Random.nextInt(4, 13)
    return (0 until numClusters).map {
        (0 until numSeries).map {
            Random.nextFloat()
        }.toCluster()
    }.toRectangularDataSet()
}
