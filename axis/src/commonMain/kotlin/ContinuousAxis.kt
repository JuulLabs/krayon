package com.juul.krayon.axis

import com.juul.krayon.element.Element
import com.juul.krayon.element.LineElement
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.scale.ContinuousScale
import com.juul.krayon.scale.Ticker
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.append
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.insert
import com.juul.krayon.selection.keyedData
import com.juul.krayon.selection.merge
import com.juul.krayon.selection.order
import com.juul.krayon.selection.remove
import com.juul.krayon.selection.select
import com.juul.krayon.selection.selectAll
import kotlin.math.max

private fun translateX(amount: Float) = Transform.Translate(horizontal = amount)
private fun translateY(amount: Float) = Transform.Translate(vertical = amount)

// TODO: Axis should support scales other than ContinuousScale, but since we don't have other scales yet...
public class ContinuousAxis<D : Comparable<D>>(
    private val edge: Edge,
    private val scale: ContinuousScale<D, Float>,
    private val ticker: Ticker<D>,
    private val formatter: (D) -> String
) {
    private var tickSizeInner = 6f
    private var tickSizeOuter = 6f
    private var tickPadding = 3f
    private val k = if (edge == Edge.Top || edge == Edge.Left) -1 else 1
    private val isVertical = edge == Edge.Left || edge == Edge.Right
    private val transform = if (edge == Edge.Top || edge == Edge.Bottom) ::translateX else ::translateY

    public fun applySelection(selection: Selection<*, *>) {
        val values = ticker.ticks(scale.domain.minOf { it }, scale.domain.maxOf { it }, 10)
        val spacing = max(tickSizeInner, 0f) + tickPadding

        val range = scale.range
        val range0 = range.first()
        val range1 = range.last()

        val position = scale::scale

        val path = selection.selectAll(PathElement.withKind("domain"))
            .data(listOf(null))

        val tick = selection.selectAll(TransformElement.withKind("tick"))
            .keyedData(values) { (d) -> (d as? D)?.let(scale::scale) }
            .order()
        val tickEnter = tick.enter.append(TransformElement)
            .each { kind = "tick" }
        val tickExit = tick.exit

        val line = tick.select(LineElement)

        val text = tick.select(TextElement)

        val pathMerge = path.merge(
            path.enter.insert(PathElement, Element.withKind("tick"))
                .each { kind = "domain" }
        )

        val tickMerge = tick.merge(tickEnter)

        val lineMerge = line.merge(
            tickEnter.append(LineElement)
                .each { (if (isVertical) ::endX else ::endY).set(k * tickSizeInner) }
        )

        val textMerge = text.merge(
            tickEnter.append(TextElement)
                .each { (if (isVertical) ::x else ::y).set(k * spacing) }
            // TODO: Figure out the equivalent of dy here
        )

        // TODO: animations will live here, once we support animations.

        tickExit.remove()

        pathMerge.each {
            this.path = Path {
                if (isVertical) {
                    if (tickSizeInner != 0f) {
                        moveTo(k * tickSizeOuter, range0)
                        lineTo(0f, range0)
                        lineTo(0f, range1)
                        lineTo(k * tickSizeOuter, range1)
                    } else {
                        moveTo(0f, range0)
                        lineTo(0f, range1)
                    }
                } else {
                    if (tickSizeInner != 0f) {
                        moveTo(range0, k * tickSizeOuter)
                        lineTo(range0, 0f)
                        lineTo(range1, 0f)
                        lineTo(range1, k * tickSizeOuter)
                    } else {
                        moveTo(range0, 0f)
                        lineTo(range1, 0f)
                    }
                }
            }
        }

        tickMerge.each { (d) ->
            this.transform = transform(position(d))
        }

        lineMerge.each {
            (if (isVertical) ::endX else ::endY).set(k * tickSizeInner)
        }

        textMerge.each { (d) ->
            (if (isVertical) ::x else ::y).set(k * spacing)
            this.text = formatter(d)
        }
    }
}
