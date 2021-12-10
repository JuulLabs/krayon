package com.juul.krayon.sample

import com.juul.krayon.axis.ContinuousAxis
import com.juul.krayon.axis.Edge
import com.juul.krayon.color.darkSlateBlue
import com.juul.krayon.color.steelBlue
import com.juul.krayon.color.white
import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.GroupElement
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.extent
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.scale.ticks
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
    val margin = 50f
    val x = scale()
        .domain(data.extent { it?.x })
        .range(margin, width - margin)
    val y = scale()
        .domain(-1f, 1f)
        .range(height - margin, margin)

    val line = line<Point>()
        .x { (p) -> x.scale(p.x) }
        .y { (p) -> y.scale(p.y) }

    root.asSelection()
        .selectAll(PathElement)
        .data(listOf(data.filterNotNull(), data))
        .join(onEnter = { append(PathElement).each { (_, i) -> paint = if (i == 0) dashedLinePaint else solidLinePaint } })
        .each { (d) ->
            path = line.render(d)
        }

    root.asSelection()
        .selectAll(CircleElement)
        .data(data.filterNotNull())
        .join(
            onEnter = {
                append(CircleElement).each {
                    radius = 3f
                    paint = circlePaint
                }
            }
        ).each { (d) ->
            centerX = x.scale(d.x)
            centerY = y.scale(d.y)
        }

    val xAxisGroup = root.asSelection()
        .selectAll(GroupElement.withKind("x-axis"))
        .data(listOf(null))
        .join(onEnter = { append(GroupElement).each { kind = "x-axis" } })

    ContinuousAxis(
        Edge.Bottom,
        x,
        ticker = { start, stop, count -> ticks(start, stop, count) },
        formatter = { it.toString() }
    ).applySelection(xAxisGroup)


    val yAxisGroup = root.asSelection()
        .selectAll(GroupElement.withKind("y-axis"))
        .data(listOf(null))
        .join(onEnter = { append(GroupElement).each { kind = "y-axis" } })

    ContinuousAxis(
        Edge.Left,
        y,
        ticker = { start, stop, count -> ticks(start, stop, count) },
        formatter = { it.toString() }
    ).applySelection(yAxisGroup)
}
