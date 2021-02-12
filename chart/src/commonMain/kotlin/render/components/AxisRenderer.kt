package com.juul.krayon.chart.render.composite

import com.juul.krayon.canvas.Canvas
import com.juul.krayon.canvas.Paint
import com.juul.krayon.chart.render.Orientation
import com.juul.krayon.chart.render.Renderer

public class AxisRenderer(
    private val style: Style,
) : Renderer<AxisRenderer.Specification> {

    public class Style(
        public val orientation: Orientation,
        public val stroke: Paint.Stroke?,
    )

    public data class Specification(
        val startX: Float,
        val startY: Float,
        val end: Float,
    )

    override fun <PAINT, PATH> render(data: Specification, canvas: Canvas<PAINT, PATH>) {
        val strokePaint = style.stroke ?: return
        val (endX, endY) = when (style.orientation) {
            Orientation.Horizontal -> data.end to data.startY
            Orientation.Vertical -> data.startX to data.end
        }
        canvas.drawLine(data.startX, data.startY, endX, endY, canvas.buildPaint(strokePaint))
    }
}
