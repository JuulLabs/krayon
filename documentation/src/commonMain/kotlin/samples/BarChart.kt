package com.juul.krayon.documentation.samples

import com.juul.krayon.axis.axisLeft
import com.juul.krayon.axis.call
import com.juul.krayon.color.black
import com.juul.krayon.color.steelBlue
import com.juul.krayon.element.GroupElement
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
import kotlin.math.roundToInt

/** Relative frequency of letters in English text (D3's classic "alphabet" dataset). */
val letterFrequency: List<Pair<String, Float>> = listOf(
    "A" to 0.0817f, "B" to 0.0149f, "C" to 0.0278f, "D" to 0.0425f, "E" to 0.1270f,
    "F" to 0.0223f, "G" to 0.0202f, "H" to 0.0609f, "I" to 0.0697f, "J" to 0.0015f,
    "K" to 0.0077f, "L" to 0.0403f, "M" to 0.0241f, "N" to 0.0675f, "O" to 0.0751f,
    "P" to 0.0193f, "Q" to 0.0010f, "R" to 0.0599f, "S" to 0.0633f, "T" to 0.0906f,
    "U" to 0.0276f, "V" to 0.0098f, "W" to 0.0236f, "X" to 0.0015f, "Y" to 0.0197f,
    "Z" to 0.0007f,
)

private val barPaint = Paint.Fill(steelBlue)
private val labelPaint = Paint.Text(black, 11f, Paint.Text.Alignment.Center, Font(sansSerif))

fun barChart(root: RootElement, width: Float, height: Float, data: List<Pair<String, Float>>) {
    val margin = Margin(top = 16f, right = 8f, bottom = 24f, left = 40f)
    val innerWidth = width - margin.left - margin.right
    val innerHeight = height - margin.top - margin.bottom

    // There is no band scale (yet), so bar positions are simple index math.
    val bandWidth = innerWidth / data.size
    val y = scale()
        .domain(0f, data.maxOf { (_, frequency) -> frequency })
        .range(innerHeight, 0f)

    val body = root.asSelection()
        .selectAll(TransformElement.withKind("body"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "body" } }
        .each { transform = Transform.Translate(margin.left, margin.top) }

    body.selectAll(GroupElement.withKind("y-axis"))
        .data(listOf(null))
        .join { append(GroupElement).each { kind = "y-axis" } }
        .call(
            axisLeft(y).apply {
                formatter = { value -> "${(value * 100).roundToInt()}%" }
            },
        )

    body.selectAll(RectangleElement)
        .data(data)
        .join(RectangleElement)
        .each { (datum, index) ->
            val (_, frequency) = datum
            left = index * bandWidth + bandWidth * 0.1f
            right = (index + 1) * bandWidth - bandWidth * 0.1f
            top = y.scale(frequency)
            bottom = innerHeight
            paint = barPaint
        }

    body.selectAll(TextElement.withKind("label"))
        .data(data)
        .join { append(TextElement).each { kind = "label" } }
        .each { (datum, index) ->
            text = datum.first
            this.x = (index + 0.5f) * bandWidth
            this.y = innerHeight + 14f
            paint = labelPaint
        }
}
