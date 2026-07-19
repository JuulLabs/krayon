package com.juul.krayon.documentation.samples

import com.juul.krayon.axis.axisBottom
import com.juul.krayon.axis.axisLeft
import com.juul.krayon.axis.call
import com.juul.krayon.color.steelBlue
import com.juul.krayon.element.GroupElement
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.extent
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import com.juul.krayon.shape.line

/** Hourly temperature (°C) over a summer day. */
val hourlyTemperature: List<Point> = listOf(
    Point(0f, 14.9f), Point(1f, 14.2f), Point(2f, 13.7f), Point(3f, 13.4f),
    Point(4f, 13.1f), Point(5f, 13.3f), Point(6f, 14.4f), Point(7f, 16.2f),
    Point(8f, 18.4f), Point(9f, 20.6f), Point(10f, 22.6f), Point(11f, 24.3f),
    Point(12f, 25.7f), Point(13f, 26.8f), Point(14f, 27.4f), Point(15f, 27.5f),
    Point(16f, 27.1f), Point(17f, 26.2f), Point(18f, 24.8f), Point(19f, 23.0f),
    Point(20f, 21.0f), Point(21f, 19.2f), Point(22f, 17.6f), Point(23f, 16.3f),
)

private val linePaint = Paint.Stroke(steelBlue, 2f, join = Paint.Stroke.Join.Round)

fun lineChart(root: RootElement, width: Float, height: Float, data: List<Point>) {
    val margin = Margin(top = 16f, right = 16f, bottom = 32f, left = 40f)
    val innerWidth = width - margin.left - margin.right
    val innerHeight = height - margin.top - margin.bottom

    val x = scale()
        .domain(data.extent { it.x })
        .range(0f, innerWidth)
    val y = scale()
        .domain(data.extent { it.y })
        .range(innerHeight, 0f)

    // A line generator turns a list of data points into a Path.
    val temperatureLine = line<Point>()
        .x { (point) -> x.scale(point.x) }
        .y { (point) -> y.scale(point.y) }

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
                formatter = { hour -> "${hour.toInt()}:00" }
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

    // The "line" kind keeps this join from matching the axes' internal PathElements.
    body.selectAll(PathElement.withKind("line"))
        .data(listOf(data))
        .join { append(PathElement).each { kind = "line" } }
        .each { (points) ->
            path = temperatureLine.render(points)
            paint = linePaint
        }
}
