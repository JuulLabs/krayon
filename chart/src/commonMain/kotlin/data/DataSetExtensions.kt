package com.juul.krayon.chart.data

public fun DataSet<Float>.maxValue(): Float =
    seriesData.maxOf { series -> series.maxOf { entity -> entity } }

public fun DataSet<Float>.minValue(): Float =
    seriesData.minOf { series -> series.minOf { entity -> entity } }
