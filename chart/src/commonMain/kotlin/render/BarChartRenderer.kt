package com.juul.krayon.chart.render

import com.juul.krayon.canvas.Canvas
import com.juul.krayon.canvas.Paint
import com.juul.krayon.canvas.Transform
import com.juul.krayon.canvas.withTransform
import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.data.maxValue
import com.juul.krayon.chart.style.BarChartStyle

public open class BarChartRenderer(
    public val style: BarChartStyle,
) : Renderer<ClusteredDataSet<Float>> {

    override fun <PAINT, PATH> render(dataSet: ClusteredDataSet<Float>, canvas: Canvas<PAINT, PATH>) {
        val paints = style.seriesColors.take(dataSet.seriesData.size)
            .map { color -> canvas.buildPaint(Paint.Stroke(color, 8f, Paint.Stroke.Cap.Round)) }
            .toList()

        val scale = canvas.height / dataSet.maxValue() / 2

        canvas.withTransform(Transform.Scale(vertical = -1f, pivotY = canvas.height / 2f)) {
            var offset = 24f
            for (cluster in dataSet.clusterData) {
                for ((index, value) in cluster.withIndex()) {
                    drawLine(offset, 0f, offset, value * scale, paints[index])
                    offset += 8f
                }
                offset += 16f
            }
        }
    }
}
