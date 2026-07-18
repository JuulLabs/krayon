package com.juul.krayon.documentation.samples

import com.juul.krayon.axis.axisBottom
import com.juul.krayon.axis.axisLeft
import com.juul.krayon.axis.call
import com.juul.krayon.color.darkSlateBlue
import com.juul.krayon.color.steelBlue
import com.juul.krayon.color.white
import com.juul.krayon.element.CircleElement
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
import kotlin.math.sin

/** A sampled sine wave where every fifth measurement is missing (`null`). */
val patchySineWave: List<Point?> = (0 until 50).map { i ->
    val x = i / 49f * 6.283f
    Point(x, sin(x)).takeUnless { i % 5 == 0 }
}

private val solidLinePaint = Paint.Stroke(steelBlue, 2f)
private val dashedLinePaint = Paint.Stroke(darkSlateBlue, 1f, dash = Paint.Stroke.Dash.Pattern(5f, 5.5f))
private val circlePaint = Paint.FillAndStroke(
    Paint.Fill(white),
    Paint.Stroke(steelBlue, 1f),
)

fun missingDataLineChart(root: RootElement, width: Float, height: Float, data: List<Point?>) {
    val margin = Margin(top = 16f, right = 16f, bottom = 32f, left = 40f)
    val innerWidth = width - margin.left - margin.right
    val innerHeight = height - margin.top - margin.bottom

    val x = scale()
        .domain(data.extent { it?.x })
        .range(0f, innerWidth)
    val y = scale()
        .domain(-1f, 1f)
        .range(innerHeight, 0f)

    val sineLine = line<Point>()
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
        .call(axisBottom(x))

    body.selectAll(GroupElement.withKind("y-axis"))
        .data(listOf(null))
        .join { append(GroupElement).each { kind = "y-axis" } }
        .call(axisLeft(y))

    // Two lines from the same dataset: a dashed line ignoring the gaps (index 0),
    // and a solid line where `null` data points break the path (index 1).
    body.selectAll(PathElement.withKind("line"))
        .data(listOf(data.filterNotNull(), data))
        .join {
            append(PathElement).each { (_, index) ->
                kind = "line"
                paint = if (index == 0) dashedLinePaint else solidLinePaint
            }
        }
        .each { (points) ->
            path = sineLine.render(points)
        }

    body.selectAll(CircleElement)
        .data(data.filterNotNull())
        .join {
            append(CircleElement).each {
                radius = 3f
                paint = circlePaint
            }
        }
        .each { (point) ->
            centerX = x.scale(point.x)
            centerY = y.scale(point.y)
        }
}
