package com.juul.krayon.chart.style

import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.Font
import com.juul.krayon.chart.render.IndexLabelFactory

internal fun defaultFont(): Font = Font("sans-serif")

internal fun defaultIndexLabelFactory() = IndexLabelFactory { null }

internal fun defaultSeriesColors(): Sequence<Color> = sequenceOf(
    Color.red,
    Color.green,
    Color.blue,
    Color.cyan,
    Color.magenta,
    Color.yellow
)
