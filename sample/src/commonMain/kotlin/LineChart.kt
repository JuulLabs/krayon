package com.juul.krayon.sample

import com.juul.krayon.axis.axisBottom
import com.juul.krayon.axis.axisLeft
import com.juul.krayon.axis.call
import com.juul.krayon.color.black
import com.juul.krayon.color.darkSlateBlue
import com.juul.krayon.color.steelBlue
import com.juul.krayon.color.white
import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.GroupElement
import com.juul.krayon.element.KanvasElement
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.svg.toPath
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

private val solidLinePaint = Paint.Stroke(steelBlue, 1f)
private val dashedLinePaint = Paint.Stroke(darkSlateBlue, 0.5f, dash = Paint.Stroke.Dash.Pattern(5f, 5.5f))
private val circlePaint = Paint.FillAndStroke(
    Paint.Fill(white),
    Paint.Stroke(steelBlue, 1f)
)

internal fun lineChart(root: RootElement, width: Float, height: Float, data: List<Point?>) {
    val leftMargin = 40f
    val topMargin = 20f
    val rightMargin = 20f
    val bottomMargin = 40f

    val innerWidth = width - leftMargin - rightMargin
    val innerHeight = height - topMargin - bottomMargin

    val x = scale()
        .domain(data.extent { it?.x })
        .range(0f, innerWidth)
    val y = scale()
        .domain(-1f, 1f)
        .range(innerHeight, 0f)

    val line = line<Point>()
        .x { (p) -> x.scale(p.x) }
        .y { (p) -> y.scale(p.y) }

    root.asSelection()
        .selectAll(KanvasElement)
        .data(listOf(null))
        .join(KanvasElement)
        .each {
            onDraw = {
                drawPath("M0,0l100,100l100,-50z".toPath(), Paint.Stroke(black, 2f))
            }
        }

    val body = root.asSelection()
        .selectAll(TransformElement.withKind("body"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "body" } }
        .each {
            transform = Transform.Translate(
                horizontal = leftMargin,
                vertical = topMargin
            )
        }

    body.selectAll(TransformElement.withKind("x-axis"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "x-axis" } }
        .each { transform = Transform.Translate(vertical = innerHeight) }
        .call(axisBottom(x))

    body.selectAll(GroupElement.withKind("y-axis"))
        .data(listOf(null))
        .join { append(GroupElement).each { kind = "y-axis" } }
        .call(axisLeft(y))

    body.selectAll(PathElement.withKind("line"))
        .data(listOf(data.filterNotNull(), data))
        .join {
            append(PathElement).each { (_, i) ->
                kind = "line"
                paint = if (i == 0) dashedLinePaint else solidLinePaint
            }
        }.each { (d) ->
            path = line.render(d)
        }

    body.selectAll(CircleElement)
        .data(data.filterNotNull())
        .join {
            append(CircleElement).each {
                radius = 3f
                paint = circlePaint
            }
        }.each { (d) ->
            centerX = x.scale(d.x)
            centerY = y.scale(d.y)
        }
}
