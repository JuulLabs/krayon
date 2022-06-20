package com.juul.krayon.box

import com.juul.krayon.element.RectangleElement
import com.juul.krayon.hierarchy.treemap.Tile

public fun RectangleElement.setShapeFrom(tile: Tile) {
    left = tile.left
    top = tile.top
    right = tile.right
    bottom = tile.bottom
}
