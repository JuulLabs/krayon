package com.juul.krayon.documentation.samples

import com.juul.krayon.axis.axisBottom
import com.juul.krayon.axis.axisLeft
import com.juul.krayon.axis.call
import com.juul.krayon.color.Color
import com.juul.krayon.color.crimson
import com.juul.krayon.color.forestGreen
import com.juul.krayon.color.steelBlue
import com.juul.krayon.element.GroupElement
import com.juul.krayon.element.PathElement
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
import com.juul.krayon.shape.line
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

data class Series(
    val name: String,
    val color: Color,
    val points: List<Pair<LocalDateTime, Float>>,
)

private fun month(month: Int) = LocalDateTime(2024, month, 1, 0, 0)

private fun series(name: String, color: Color, vararg temperatures: Float) = Series(
    name = name,
    color = color,
    points = temperatures.mapIndexed { index, temperature -> month(index + 1) to temperature },
)

/** Monthly average temperature (°C) for three cities. */
val cityTemperatures: List<Series> = listOf(
    series("Lisbon", crimson, 11.6f, 12.7f, 14.5f, 15.6f, 17.8f, 20.9f, 23.1f, 23.5f, 22.1f, 18.8f, 15.0f, 12.4f),
    series("London", steelBlue, 5.2f, 5.3f, 7.6f, 9.9f, 13.3f, 16.5f, 18.7f, 18.5f, 15.7f, 12.0f, 8.0f, 5.5f),
    series("Oslo", forestGreen, -4.3f, -4.0f, -0.2f, 4.5f, 10.8f, 15.2f, 16.4f, 15.2f, 10.8f, 6.3f, 0.7f, -3.1f),
)

private val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
private val labelFont = Font(sansSerif)

fun multiSeriesLineChart(root: RootElement, width: Float, height: Float, data: List<Series>) {
    val margin = Margin(top = 16f, right = 64f, bottom = 32f, left = 40f)
    val innerWidth = width - margin.left - margin.right
    val innerHeight = height - margin.top - margin.bottom

    val allPoints = data.flatMap { it.points }
    val x = scale()
        .domain(month(1), month(12))
        .range(0f, innerWidth)
    val y = scale()
        .domain(allPoints.minOf { (_, temperature) -> temperature }, allPoints.maxOf { (_, temperature) -> temperature })
        .range(innerHeight, 0f)

    val temperatureLine = line<Pair<LocalDateTime, Float>>()
        .x { (point) -> x.scale(point.first) }
        .y { (point) -> y.scale(point.second) }

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
                formatter = { dateTime -> monthNames[dateTime.month.number - 1] }
            },
        )

    body.selectAll(GroupElement.withKind("y-axis"))
        .data(listOf(null))
        .join { append(GroupElement).each { kind = "y-axis" } }
        .call(
            axisLeft(y).apply {
                formatter = { celsius -> "${celsius.toInt()}°" }
            },
        )

    body.selectAll(PathElement)
        .data(data)
        .join(PathElement)
        .each { (series) ->
            path = temperatureLine.render(series.points)
            paint = Paint.Stroke(series.color, 2f, join = Paint.Stroke.Join.Round)
        }

    // Direct labeling at the end of each line beats a separate legend.
    body.selectAll(TextElement)
        .data(data)
        .join(TextElement)
        .each { (series) ->
            val (lastMonth, lastTemperature) = series.points.last()
            text = series.name
            this.x = x.scale(lastMonth) + 8f
            this.y = y.scale(lastTemperature) + 4f
            paint = Paint.Text(series.color, 12f, Paint.Text.Alignment.Left, labelFont)
        }
}
