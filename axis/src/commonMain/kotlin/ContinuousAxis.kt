package com.juul.krayon.axis

import com.juul.krayon.element.PathElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.scale.ContinuousScale
import com.juul.krayon.scale.Ticker
import com.juul.krayon.selection.Selection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.selectAll
import kotlin.math.max

private fun translateX(amount: Float) = Transform.Translate(horizontal = amount)
private fun translateY(amount: Float) = Transform.Translate(vertical = amount)

// TODO: Axis should support scales other than ContinuousScale, but since we don't have other scales yet...
public class ContinuousAxis<D : Comparable<D>, R>(
    private val edge: Edge,
    private val scale: ContinuousScale<D, R>,
    private val ticker: Ticker<D>,
) {
    private var tickSizeInner = 6f
    private var tickSizeOuter = 6f
    private var tickPadding = 3f
    private val k = if (edge == Edge.Top || edge == Edge.Left) -1 else 1
    private val x = if (edge == Edge.Left || edge == Edge.Right) "x" else "y"
    private val transform = if (edge == Edge.Top || edge == Edge.Bottom) ::translateX else ::translateY

    public fun applySelection(selection: Selection<*, *>) {
        val values = ticker.ticks(scale.domain.minOf { it }, scale.domain.maxOf { it }, 10)
        val spacing = max(tickSizeInner, 0f) + tickPadding

        val path = selection.selectAll(PathElement.withKind("domain"))
            .data(listOf(null))
        val tick = selection.selectAll(TransformElement.withKind("tick"))

        // STOPSHIP: Continue working from here, once data with a key is supported.
    }
}
