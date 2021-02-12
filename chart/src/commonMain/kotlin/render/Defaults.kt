package com.juul.krayon.chart.render

import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.Font
import com.juul.krayon.canvas.Paint
import com.juul.krayon.canvas.lerp
import com.juul.krayon.chart.render.components.AxisRenderer
import com.juul.krayon.chart.render.components.BarRenderer

internal fun defaultAxisStyle(
    orientation: Orientation
): AxisRenderer.Style = AxisRenderer.Style(
    orientation,
    Paint.Stroke(Color.black, 1f, Paint.Stroke.Cap.Square)
)
internal fun defaultBarStyle(
    orientation: Orientation
): BarRenderer.Style = BarRenderer.Style(
    orientation,
    defaultSeriesColors(),
    BarRenderer.ClusterBehavior.Grouped
)

internal fun defaultFont(): Font = Font("sans-serif")

internal fun defaultIndexLabelFactory() = IndexLabelFactory { null }

internal fun defaultSeriesColors(): Sequence<Color> = sequenceOf(
    Color.red.lerp(Color.white, 0.375f),
    Color.green.lerp(Color.white, 0.375f),
    Color.blue.lerp(Color.white, 0.375f),
    Color.cyan.lerp(Color.white, 0.375f),
    Color.magenta.lerp(Color.white, 0.375f),
    Color.yellow.lerp(Color.white, 0.375f),
    Color.red.lerp(Color.white, 0.75f),
    Color.green.lerp(Color.white, 0.75f),
    Color.blue.lerp(Color.white, 0.75f),
    Color.cyan.lerp(Color.white, 0.75f),
    Color.magenta.lerp(Color.white, 0.75f),
    Color.yellow.lerp(Color.white, 0.75f)
)
