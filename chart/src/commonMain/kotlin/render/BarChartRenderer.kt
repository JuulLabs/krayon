package com.juul.krayon.chart.render

import com.juul.krayon.canvas.Canvas
import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.render.composite.AxisRenderer
import com.juul.krayon.chart.render.composite.BarRenderer

private const val AXIS_PAD = 24f

/** Renderer for bar charts. */
public open class BarChartRenderer(
    style: Style = Style(),
) : Renderer<ClusteredDataSet<Float>> {

    public class Style(
        public val xAxisStyle: AxisRenderer.Style = defaultAxisStyle(Orientation.Horizontal),
        public val yAxisStyle: AxisRenderer.Style = defaultAxisStyle(Orientation.Vertical),
        public val barStyle: BarRenderer.Style = defaultBarStyle(Orientation.Vertical),
    ) {
    }

    private val xAxisRenderer = AxisRenderer(style.xAxisStyle)
    private val yAxisRenderer = AxisRenderer(style.yAxisStyle)
    private val barRenderer = BarRenderer(style.barStyle)

    override fun <PAINT, PATH> render(data: ClusteredDataSet<Float>, canvas: Canvas<PAINT, PATH>) {
        val dataAreaLeft = AXIS_PAD
        val dataAreaTop = AXIS_PAD
        val dataAreaRight = canvas.width - AXIS_PAD
        val dataAreaBottom = canvas.height - AXIS_PAD

        barRenderer.render(BarRenderer.Specification(dataAreaLeft, dataAreaTop, dataAreaRight, dataAreaBottom, data), canvas)
        xAxisRenderer.render(AxisRenderer.Specification(dataAreaLeft, dataAreaBottom, dataAreaRight), canvas)
        yAxisRenderer.render(AxisRenderer.Specification(dataAreaLeft, dataAreaBottom, dataAreaTop), canvas)
    }
}
