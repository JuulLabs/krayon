package com.juul.krayon.documentation.samples

import com.juul.krayon.axis.axisBottom
import com.juul.krayon.axis.call
import com.juul.krayon.color.black
import com.juul.krayon.color.steelBlue
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

/** Estimated worldwide speakers (in millions) of a few languages. */
val languageSpeakers: List<Pair<String, Float>> = listOf(
    "English" to 1515f,
    "Mandarin" to 1140f,
    "Hindi" to 609f,
    "Spanish" to 560f,
    "Arabic" to 332f,
    "French" to 312f,
    "Bengali" to 278f,
    "Portuguese" to 264f,
)

private val barPaint = Paint.Fill(steelBlue)
private val labelPaint = Paint.Text(black, 12f, Paint.Text.Alignment.Right, Font(sansSerif))

fun horizontalBarChart(root: RootElement, width: Float, height: Float, data: List<Pair<String, Float>>) {
    val margin = Margin(top = 8f, right = 16f, bottom = 32f, left = 80f)
    val innerWidth = width - margin.left - margin.right
    val innerHeight = height - margin.top - margin.bottom

    val x = scale()
        .domain(0f, data.maxOf { (_, speakers) -> speakers })
        .range(0f, innerWidth)
    val bandHeight = innerHeight / data.size

    val body = root.asSelection()
        .selectAll(TransformElement.withKind("body"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "body" } }
        .each { transform = Transform.Translate(margin.left, margin.top) }

    body.selectAll(TransformElement.withKind("x-axis"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "x-axis" } }
        .each { transform = Transform.Translate(vertical = innerHeight) }
        .call(
            axisBottom(x).apply {
                tickCount = 4
                formatter = { value -> "${value.toInt()} M" }
            },
        )

    body.selectAll(RectangleElement)
        .data(data)
        .join(RectangleElement)
        .each { (datum, index) ->
            val (_, speakers) = datum
            left = 0f
            right = x.scale(speakers)
            top = index * bandHeight + bandHeight * 0.1f
            bottom = (index + 1) * bandHeight - bandHeight * 0.1f
            paint = barPaint
        }

    body.selectAll(TextElement.withKind("label"))
        .data(data)
        .join { append(TextElement).each { kind = "label" } }
        .each { (datum, index) ->
            text = datum.first
            this.x = -6f
            this.y = (index + 0.5f) * bandHeight + 4f
            paint = labelPaint
        }
}
