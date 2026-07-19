package com.juul.krayon.hierarchy.cluster

import com.juul.krayon.hierarchy.Node
import com.juul.krayon.hierarchy.Point
import com.juul.krayon.hierarchy.traversePostOrder

/** Applies [cluster] to `this`, attaching a [Point] layout to every node. */
public fun <T> Node<T, *>.layoutWith(cluster: Cluster<T>): Node<T, Point> = cluster.layout(this)

/**
 * Dendrogram (cluster) layout that places every leaf at the same depth.
 *
 * When [nodeSize] is `false`, [width] and [height] are the extent of the layout; otherwise they are the
 * per-node spacing along the x and y axes.
 */
public class Cluster<T>(
    public var width: Float = 1f,
    public var height: Float = 1f,
    public var nodeSize: Boolean = false,
    public var separation: (Node<T, Point>, Node<T, Point>) -> Float = defaultSeparation(),
) {
    public fun layout(root: Node<T, *>): Node<T, Point> {
        @Suppress("UNCHECKED_CAST")
        val typedRoot = root as Node<T, Point>
        val xs = HashMap<Node<T, Point>, Float>()
        val ys = HashMap<Node<T, Point>, Float>()

        var previousNode: Node<T, Point>? = null
        var x = 0f
        typedRoot.traversePostOrder().forEach { node ->
            val children = node.children
            if (children.isNotEmpty()) {
                xs[node] = children.sumOf { xs.getValue(it).toDouble() }.toFloat() / children.size
                ys[node] = 1f + children.fold(0f) { max, child -> maxOf(max, ys.getValue(child)) }
            } else {
                val previous = previousNode
                xs[node] = if (previous != null) {
                    x += separation(node, previous)
                    x
                } else {
                    0f
                }
                ys[node] = 0f
                previousNode = node
            }
        }

        val left = leafLeft(typedRoot)
        val right = leafRight(typedRoot)
        val x0 = xs.getValue(left) - separation(left, right) / 2f
        val x1 = xs.getValue(right) + separation(right, left) / 2f
        val rootX = xs.getValue(typedRoot)
        val rootY = ys.getValue(typedRoot)

        typedRoot.traversePostOrder().forEach { node ->
            node.layout = if (nodeSize) {
                Point((xs.getValue(node) - rootX) * width, (rootY - ys.getValue(node)) * height)
            } else {
                val normalizedY = if (rootY != 0f) ys.getValue(node) / rootY else 1f
                Point((xs.getValue(node) - x0) / (x1 - x0) * width, (1f - normalizedY) * height)
            }
        }
        return typedRoot
    }
}

/** Default node separation: `1` between siblings, `2` between nodes with different parents. */
public fun <T> defaultSeparation(): (Node<T, Point>, Node<T, Point>) -> Float =
    { a, b -> if (a.parent === b.parent) 1f else 2f }

private fun <T> leafLeft(node: Node<T, Point>): Node<T, Point> {
    var current = node
    while (current.children.isNotEmpty()) current = current.children.first()
    return current
}

private fun <T> leafRight(node: Node<T, Point>): Node<T, Point> {
    var current = node
    while (current.children.isNotEmpty()) current = current.children.last()
    return current
}
