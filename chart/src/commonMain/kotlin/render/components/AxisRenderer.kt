package com.juul.krayon.chart.render.components

import com.juul.krayon.chart.render.Orientation
import com.juul.krayon.chart.render.Renderer
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint

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

    override fun <PAINT, PATH> render(data: Specification, canvas: Kanvas<PAINT, PATH>) {
        val strokePaint = style.stroke ?: return
        val (endX, endY) = when (style.orientation) {
            Orientation.Horizontal -> data.end to data.startY
            Orientation.Vertical -> data.startX to data.end
        }
        canvas.drawLine(data.startX, data.startY, endX, endY, canvas.buildPaint(strokePaint))
    }
}
