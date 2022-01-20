package com.juul.krayon.hierarchy.treemap

import com.juul.krayon.hierarchy.Node
import com.juul.krayon.hierarchy.depth

public object SliceAndDice : TileMethod {
    override fun tile(parent: Node<*, Tile>) {
        when (parent.depth % 2) {
            1 -> Slice.tile(parent)
            0 -> Dice.tile(parent)
        }
    }
}

public object Slice : TileMethod {
    override fun tile(parent: Node<*, Tile>) {
        check(parent.weight != 0f) { "Node weight has not been set. Did you forget to call `sum` (or similar)?" }
        var top = parent.layout.top
        var bottom = parent.layout.bottom
        val scale = (bottom - top) / parent.weight
        for (child in parent.children) {
            bottom = top + child.weight * scale
            child.layout = tile(parent.layout.left, top, parent.layout.right, bottom)
            top = bottom
        }
    }
}

public object Dice : TileMethod {
    override fun tile(parent: Node<*, Tile>) {
        check(parent.weight != 0f) { "Node weight has not been set. Did you forget to call `sum` (or similar)?" }
        var left = parent.layout.left
        var right = parent.layout.right
        val scale = (right - left) / parent.weight
        for (child in parent.children) {
            right = left + child.weight * scale
            child.layout = tile(left, parent.layout.top, right, parent.layout.bottom)
            left = right
        }
    }
}
