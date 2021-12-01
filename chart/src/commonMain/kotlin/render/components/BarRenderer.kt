package com.juul.krayon.chart.render.components

import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.data.maxValue
import com.juul.krayon.chart.render.Orientation
import com.juul.krayon.chart.render.Renderer
import com.juul.krayon.color.Color
import com.juul.krayon.kanvas.Clip
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.withClip

public class BarRenderer(
    private val style: Style,
) : Renderer<BarRenderer.Specification> {

    public class Style(
        public val orientation: Orientation,
        public val colors: Sequence<Color>,
        public val clusterBehavior: ClusterBehavior,
    )

    /** How to represent multiple series within the same cluster. */
    public enum class ClusterBehavior {
        /** Each series should be its own bar, grouped next to each other. */
        Grouped,

        /** Each series should stacked to make a larger bar. */
        Stacked
    }

    public data class Specification(
        public val left: Float,
        public val top: Float,
        public val right: Float,
        public val bottom: Float,
        public val bars: ClusteredDataSet<Float>,
    )

    override fun <PATH> render(data: Specification, canvas: Kanvas<PATH>) {
        val strokeWidth = 8f
        val paints = style.colors.take(data.bars.seriesData.size)
            .map { color -> Paint.Stroke(color, strokeWidth, Paint.Stroke.Cap.Round) }
            .toList()

        canvas.withClip(Clip.Rect(data.left, data.top, data.right, data.bottom)) {
            val scale = -1 * ((data.bottom - data.top - strokeWidth / 2) / data.bars.maxValue())
            var offset = data.left + 16f
            for (cluster in data.bars.clusterData) {
                for ((index, value) in cluster.withIndex()) {
                    canvas.drawLine(offset, data.bottom, offset, data.bottom + value * scale, paints[index])
                    offset += 8f
                }
                offset += 16f
            }
        }
    }
}
