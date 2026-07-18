package com.juul.krayon.documentation.samples

import com.juul.krayon.color.Color
import com.juul.krayon.color.black
import com.juul.krayon.color.crimson
import com.juul.krayon.color.gold
import com.juul.krayon.color.mediumBlue
import com.juul.krayon.color.seaGreen
import com.juul.krayon.color.steelBlue
import com.juul.krayon.color.white
import com.juul.krayon.element.RectangleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.sansSerif
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll

private data class Ramp(
    val label: String,
    val colors: List<Color>,
)

/** Scales can interpolate over any range — including colors. */
private val ramps = listOf(
    Ramp("White → SteelBlue", listOf(white, steelBlue)),
    Ramp("Crimson → Gold → SeaGreen", listOf(crimson, gold, seaGreen)),
    Ramp("Blue → White → Red", listOf(mediumBlue, white, crimson)),
)

private const val SWATCH_COUNT = 64
private val labelPaint = Paint.Text(black, 12f, Paint.Text.Alignment.Left, Font(sansSerif))

fun colorScales(root: RootElement, width: Float, height: Float, data: Unit) {
    val margin = Margin(top = 8f, right = 16f, bottom = 8f, left = 16f)
    val innerWidth = width - margin.left - margin.right
    val rowHeight = (height - margin.top - margin.bottom) / ramps.size

    val body = root.asSelection()
        .selectAll(TransformElement.withKind("body"))
        .data(listOf(data))
        .join { append(TransformElement).each { kind = "body" } }
        .each { transform = Transform.Translate(margin.left, margin.top) }

    val rows = body.selectAll(TransformElement.withKind("row"))
        .data(ramps)
        .join { append(TransformElement).each { kind = "row" } }
        .each { (_, index) ->
            transform = Transform.Translate(vertical = index * rowHeight)
        }

    rows.each { (ramp) ->
        // Distribute the ramp's colors evenly over the domain 0..1.
        val colorScale = scale()
            .domain(ramp.colors.indices.map { it.toFloat() / (ramp.colors.size - 1) })
            .range(ramp.colors)

        asSelection().selectAll(RectangleElement)
            .data(List(SWATCH_COUNT) { it.toFloat() / (SWATCH_COUNT - 1) })
            .join(RectangleElement)
            .each { (fraction, index) ->
                left = index * innerWidth / SWATCH_COUNT
                right = (index + 1) * innerWidth / SWATCH_COUNT + 0.5f
                top = 20f
                bottom = rowHeight - 12f
                paint = Paint.Fill(colorScale.scale(fraction))
            }

        asSelection().selectAll(TextElement)
            .data(listOf(ramp.label))
            .join(TextElement)
            .each { (label) ->
                text = label
                x = 0f
                y = 14f
                paint = labelPaint
            }
    }
}
