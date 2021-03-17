package com.juul.krayon.chart.render

import com.juul.krayon.chart.render.components.AxisRenderer
import com.juul.krayon.chart.render.components.BarRenderer
import com.juul.krayon.color.Color
import com.juul.krayon.color.black
import com.juul.krayon.color.blue
import com.juul.krayon.color.cyan
import com.juul.krayon.color.lerp
import com.juul.krayon.color.lime
import com.juul.krayon.color.magenta
import com.juul.krayon.color.red
import com.juul.krayon.color.white
import com.juul.krayon.color.yellow
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint

internal fun defaultAxisStyle(
    orientation: Orientation,
): AxisRenderer.Style = AxisRenderer.Style(
    orientation,
    Paint.Stroke(black, 1f, Paint.Stroke.Cap.Square)
)

internal fun defaultBarStyle(
    orientation: Orientation,
): BarRenderer.Style = BarRenderer.Style(
    orientation,
    defaultSeriesColors(),
    BarRenderer.ClusterBehavior.Grouped
)

internal fun defaultFont(): Font = Font("sans-serif")

internal fun defaultIndexLabelFactory() = IndexLabelFactory { null }

internal fun defaultSeriesColors(): Sequence<Color> = sequenceOf(
    lerp(red, white, 0.375f),
    lerp(lime, white, 0.375f),
    lerp(blue, white, 0.375f),
    lerp(cyan, white, 0.375f),
    lerp(magenta, white, 0.375f),
    lerp(yellow, white, 0.375f),
    lerp(red, white, 0.75f),
    lerp(lime, white, 0.75f),
    lerp(blue, white, 0.75f),
    lerp(cyan, white, 0.75f),
    lerp(magenta, white, 0.75f),
    lerp(yellow, white, 0.75f)
)
