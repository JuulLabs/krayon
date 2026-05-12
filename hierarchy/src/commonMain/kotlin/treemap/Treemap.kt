package com.juul.krayon.hierarchy.treemap

import com.juul.krayon.hierarchy.Node
import com.juul.krayon.hierarchy.depth
import com.juul.krayon.hierarchy.eachBefore

public fun <T> Node<T, *>.layoutWith(treemap: Treemap<T>): Node<T, Tile> = treemap.layout(this)

public class Treemap<T>(
    public var width: Float = 1f,
    public var height: Float = 1f,
    public var tileMethod: TileMethod = Squarify(),
    public var paddingLeft: (Node<T, Tile>) -> Float = { 0f },
    public var paddingTop: (Node<T, Tile>) -> Float = { 0f },
    public var paddingRight: (Node<T, Tile>) -> Float = { 0f },
    public var paddingBottom: (Node<T, Tile>) -> Float = { 0f },
    public var paddingInner: (Node<T, Tile>) -> Float = { 0f },
) {

    public fun layout(root: Node<T, *>): Node<T, Tile> {
        @Suppress("UNCHECKED_CAST")
        (root as Node<T, Tile>).layout = tile(0f, 0f, width, height)
        val paddingStack = mutableMapOf(0 to 0f)
        root.eachBefore { node ->
            var p = paddingStack.getValue(node.depth)
            val tile = tile(
                node.layout.left + p,
                node.layout.top + p,
                node.layout.right - p,
                node.layout.bottom - p,
            )
            if (node.children.isNotEmpty()) {
                p = paddingInner(node) / 2
                paddingStack[node.depth + 1] = p
                node.layout = tile(
                    tile.left + paddingLeft(node) - p,
                    tile.top + paddingTop(node) - p,
                    tile.right - paddingRight(node) + p,
                    tile.bottom - paddingBottom(node) + p,
                )
                tileMethod.tile(node)
            }
            node.layout = tile
        }
        // TODO: Add rounding.
        return root
    }
}
