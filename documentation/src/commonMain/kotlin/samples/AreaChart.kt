package com.juul.krayon.documentation.samples

import com.juul.krayon.axis.axisBottom
import com.juul.krayon.axis.axisLeft
import com.juul.krayon.axis.call
import com.juul.krayon.color.steelBlue
import com.juul.krayon.color.transparent
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
import com.juul.krayon.shape.area
import com.juul.krayon.shape.line

/** Monthly rainfall (mm). */
val monthlyRainfall: List<Point> = listOf(
    Point(1f, 69f), Point(2f, 54f), Point(3f, 51f), Point(4f, 44f),
    Point(5f, 46f), Point(6f, 34f), Point(7f, 22f), Point(8f, 27f),
    Point(9f, 43f), Point(10f, 75f), Point(11f, 88f), Point(12f, 81f),
)

private val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
private val linePaint = Paint.Stroke(steelBlue, 2f, join = Paint.Stroke.Join.Round)

fun areaChart(root: RootElement, width: Float, height: Float, data: List<Point>) {
    val margin = Margin(top = 16f, right = 16f, bottom = 32f, left = 40f)
    val innerWidth = width - margin.left - margin.right
    val innerHeight = height - margin.top - margin.bottom

    val x = scale()
        .domain(data.extent { it.x })
        .range(0f, innerWidth)
    val y = scale()
        .domain(0f, data.maxOf { it.y })
        .range(innerHeight, 0f)

    // An area generator fills between a baseline (y0) and a topline (y1).
    val rainfallArea = area<Point>()
        .x { (point) -> x.scale(point.x) }
        .y0(innerHeight)
        .y1 { (point) -> y.scale(point.y) }

    val rainfallLine = line<Point>()
        .x { (point) -> x.scale(point.x) }
        .y { (point) -> y.scale(point.y) }

    // A vertical gradient makes the fill fade towards the baseline.
    val areaPaint = Paint.Gradient.Linear(
        startX = 0f, startY = 0f,
        endX = 0f, endY = innerHeight,
        Paint.Gradient.Stop(0f, steelBlue.copy(alpha = 0x99)),
        Paint.Gradient.Stop(1f, transparent),
    )

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
                tickCount = 12
                formatter = { month -> monthNames.getOrNull(month.toInt() - 1) ?: "" }
            },
        )

    body.selectAll(GroupElement.withKind("y-axis"))
        .data(listOf(null))
        .join { append(GroupElement).each { kind = "y-axis" } }
        .call(
            axisLeft(y).apply {
                formatter = { mm -> "${mm.toInt()} mm" }
            },
        )

    body.selectAll(PathElement.withKind("area"))
        .data(listOf(data))
        .join { append(PathElement).each { kind = "area" } }
        .each { (points) ->
            path = rainfallArea.render(points)
            paint = areaPaint
        }

    body.selectAll(PathElement.withKind("line"))
        .data(listOf(data))
        .join { append(PathElement).each { kind = "line" } }
        .each { (points) ->
            path = rainfallLine.render(points)
            paint = linePaint
        }
}
