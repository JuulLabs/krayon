package com.juul.krayon.hierarchy.treemap

public fun tile(left: Float, top: Float, right: Float, bottom: Float): Tile = when {
    right < left && bottom < top -> {
        val centerX = (right - left) / 2f
        val centerY = (bottom - top) / 2f
        Tile(centerX, centerY, centerX, centerY)
    }
    right < left -> {
        val centerX = (right - left) / 2f
        Tile(centerX, top, centerX, bottom)
    }
    bottom < top -> {
        val centerY = (bottom - top) / 2f
        Tile(left, centerY, right, centerY)
    }
    else -> Tile(left, top, right, bottom)
}

public class Tile internal constructor(
    public val left: Float,
    public val top: Float,
    public val right: Float,
    public val bottom: Float,
) {
    public val width: Float get() = right - left
    public val height: Float get() = bottom - top

    public fun copy(
        left: Float = this.left,
        top: Float = this.top,
        right: Float = this.right,
        bottom: Float = this.bottom
    ): Tile = tile(left, top, right, bottom)

    override fun toString(): String = "Tile(left=$left, top=$top, right=$right, bottom=$bottom)"
}
