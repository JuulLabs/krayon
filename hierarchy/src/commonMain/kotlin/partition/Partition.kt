package com.juul.krayon.hierarchy.partition

import com.juul.krayon.hierarchy.Node
import com.juul.krayon.hierarchy.depth
import com.juul.krayon.hierarchy.height
import com.juul.krayon.hierarchy.traversePreOrder
import kotlin.math.floor

/** Rectangle attached as a layout by [Partition], spanning `[x0, x1]` horizontally and `[y0, y1]` vertically. */
public class Cell internal constructor(
    public val x0: Float,
    public val y0: Float,
    public val x1: Float,
    public val y1: Float,
) {
    public val width: Float get() = x1 - x0
    public val height: Float get() = y1 - y0

    override fun toString(): String = "Cell(x0=$x0, y0=$y0, x1=$x1, y1=$y1)"
}

/** Applies [partition] to `this`, attaching a [Cell] layout to every node. */
public fun <T> Node<T, *>.layoutWith(partition: Partition<T>): Node<T, Cell> = partition.layout(this)

/**
 * Adjacency (icicle/sunburst) layout. Subdivides [width] by [height] into rectangles sized by node weight (set via
 * `sum`/`count`), with each depth occupying a horizontal band. [padding] insets each cell; [round] snaps coordinates
 * to integers.
 */
public class Partition<T>(
    public var width: Float = 1f,
    public var height: Float = 1f,
    public var padding: Float = 0f,
    public var round: Boolean = false,
) {
    public fun layout(root: Node<T, *>): Node<T, Cell> {
        @Suppress("UNCHECKED_CAST")
        val typedRoot = root as Node<T, Cell>
        val rectangles = HashMap<Node<T, Cell>, Rectangle>()

        fun rectOf(node: Node<T, Cell>) = rectangles.getOrPut(node) { Rectangle() }

        val n = typedRoot.height + 1
        rectOf(typedRoot).apply {
            x0 = padding
            y0 = padding
            x1 = width
            y1 = height / n
        }
        typedRoot.traversePreOrder().forEach { node -> positionNode(node, n, ::rectOf) }
        if (round) {
            typedRoot.traversePreOrder().forEach { node ->
                rectOf(node).apply {
                    x0 = roundHalfUp(x0)
                    y0 = roundHalfUp(y0)
                    x1 = roundHalfUp(x1)
                    y1 = roundHalfUp(y1)
                }
            }
        }
        typedRoot.traversePreOrder().forEach { node ->
            val rectangle = rectOf(node)
            node.layout = Cell(rectangle.x0, rectangle.y0, rectangle.x1, rectangle.y1)
        }
        return typedRoot
    }

    private fun positionNode(node: Node<T, Cell>, n: Int, rectOf: (Node<T, Cell>) -> Rectangle) {
        val rectangle = rectOf(node)
        if (node.children.isNotEmpty()) {
            dice(
                node,
                rectangle.x0,
                height * (node.depth + 1) / n,
                rectangle.x1,
                height * (node.depth + 2) / n,
                rectOf,
            )
        }
        var x0 = rectangle.x0
        var y0 = rectangle.y0
        var x1 = rectangle.x1 - padding
        var y1 = rectangle.y1 - padding
        if (x1 < x0) {
            x0 = (x0 + x1) / 2f
            x1 = x0
        }
        if (y1 < y0) {
            y0 = (y0 + y1) / 2f
            y1 = y0
        }
        rectangle.x0 = x0
        rectangle.y0 = y0
        rectangle.x1 = x1
        rectangle.y1 = y1
    }

    private fun dice(
        parent: Node<T, Cell>,
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        rectOf: (Node<T, Cell>) -> Rectangle,
    ) {
        val k = if (parent.weight != 0f) (x1 - x0) / parent.weight else 0f
        var x = x0
        for (child in parent.children) {
            rectOf(child).apply {
                this.y0 = y0
                this.y1 = y1
                this.x0 = x
                x += child.weight * k
                this.x1 = x
            }
        }
    }
}

private fun roundHalfUp(value: Float): Float = floor(value + 0.5f)

private class Rectangle {
    var x0: Float = 0f
    var y0: Float = 0f
    var x1: Float = 0f
    var y1: Float = 0f
}
