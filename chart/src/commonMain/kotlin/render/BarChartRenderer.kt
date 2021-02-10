package com.juul.krayon.chart.render

import com.juul.krayon.canvas.Canvas
import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.style.BarChartStyle

public open class BarChartRenderer(
    public val style: BarChartStyle
) : Renderer<ClusteredDataSet<Float>> {
    override fun render(dataSet: ClusteredDataSet<Float>, canvas: Canvas) {
        TODO("Not yet implemented")
    }
}
