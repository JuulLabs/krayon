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

@Deprecated("Chart module removed in favor of D3-like paradigm.")
internal fun defaultAxisStyle(
    orientation: Orientation,
): AxisRenderer.Style = AxisRenderer.Style(
    orientation,
    Paint.Stroke(black, 1f, Paint.Stroke.Cap.Square)
)

@Deprecated("Chart module removed in favor of D3-like paradigm.")
internal fun defaultBarStyle(
    orientation: Orientation,
): BarRenderer.Style = BarRenderer.Style(
    orientation,
    defaultSeriesColors(),
    BarRenderer.ClusterBehavior.Grouped
)

@Deprecated("Chart module removed in favor of D3-like paradigm.")
internal fun defaultFont(): Font = Font("sans-serif")

@Deprecated("Chart module removed in favor of D3-like paradigm.")
internal fun defaultIndexLabelFactory() = IndexLabelFactory { null }

@Deprecated("Chart module removed in favor of D3-like paradigm.")
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
