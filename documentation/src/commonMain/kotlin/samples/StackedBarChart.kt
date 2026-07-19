package com.juul.krayon.documentation.samples

import com.juul.krayon.axis.axisLeft
import com.juul.krayon.axis.call
import com.juul.krayon.color.Color
import com.juul.krayon.color.black
import com.juul.krayon.color.cornflowerBlue
import com.juul.krayon.color.goldenrod
import com.juul.krayon.color.seaGreen
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

data class QuarterlySales(
    val quarter: String,
    val sales: Map<String, Float>,
)

val productColors: Map<String, Color> = mapOf(
    "Laptops" to cornflowerBlue,
    "Phones" to seaGreen,
    "Tablets" to goldenrod,
)

/** Quarterly unit sales (in thousands), by product line. */
val quarterlySales: List<QuarterlySales> = listOf(
    QuarterlySales("Q1", mapOf("Laptops" to 42f, "Phones" to 78f, "Tablets" to 24f)),
    QuarterlySales("Q2", mapOf("Laptops" to 48f, "Phones" to 71f, "Tablets" to 28f)),
    QuarterlySales("Q3", mapOf("Laptops" to 39f, "Phones" to 86f, "Tablets" to 31f)),
    QuarterlySales("Q4", mapOf("Laptops" to 61f, "Phones" to 104f, "Tablets" to 38f)),
)

/** One rectangle of the stacked chart: a product's contribution to a quarter. */
private data class Layer(
    val quarterIndex: Int,
    val quarter: String,
    val product: String,
    val stackStart: Float,
    val stackEnd: Float,
)

private val labelPaint = Paint.Text(black, 12f, Paint.Text.Alignment.Center, Font(sansSerif))

fun stackedBarChart(root: RootElement, width: Float, height: Float, data: List<QuarterlySales>) {
    val margin = Margin(top = 16f, right = 16f, bottom = 32f, left = 40f)
    val innerWidth = width - margin.left - margin.right
    val innerHeight = height - margin.top - margin.bottom

    // Krayon has no stack generator (yet), so stack by accumulating a running total per quarter.
    val layers = data.flatMapIndexed { index, (quarter, sales) ->
        var runningTotal = 0f
        productColors.keys.map { product ->
            val value = sales.getOrElse(product) { 0f }
            Layer(index, quarter, product, runningTotal, runningTotal + value)
                .also { runningTotal += value }
        }
    }

    val bandWidth = innerWidth / data.size
    val y = scale()
        .domain(0f, layers.maxOf { it.stackEnd })
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
                formatter = { thousands -> "${thousands.toInt()}k" }
            },
        )

    body.selectAll(RectangleElement)
        .data(layers)
        .join(RectangleElement)
        .each { (layer) ->
            left = layer.quarterIndex * bandWidth + bandWidth * 0.15f
            right = (layer.quarterIndex + 1) * bandWidth - bandWidth * 0.15f
            top = y.scale(layer.stackEnd)
            bottom = y.scale(layer.stackStart)
            paint = Paint.Fill(checkNotNull(productColors[layer.product]))
        }

    body.selectAll(TextElement.withKind("label"))
        .data(data)
        .join { append(TextElement).each { kind = "label" } }
        .each { (quarterly, index) ->
            text = quarterly.quarter
            this.x = (index + 0.5f) * bandWidth
            this.y = innerHeight + 16f
            paint = labelPaint
        }
}
