package com.juul.krayon.chart.render

import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.style.BarChartStyle
import com.juul.krayon.kanvas.Kanvas

public open class BarChartRenderer(
    public val style: BarChartStyle,
) : Renderer<ClusteredDataSet<Float>> {
    override fun <PAINT, PATH> render(dataSet: ClusteredDataSet<Float>, canvas: Kanvas<PAINT, PATH>) {
        TODO("Not yet implemented")
    }
}
