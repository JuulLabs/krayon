package com.juul.krayon.chart.data

@Deprecated("Chart module removed in favor of D3-like paradigm.")
public fun DataSet<Float>.maxValue(): Float =
    seriesData.maxOf { series -> series.maxOf { entity -> entity } }

@Deprecated("Chart module removed in favor of D3-like paradigm.")
public fun DataSet<Float>.minValue(): Float =
    seriesData.minOf { series -> series.minOf { entity -> entity } }
