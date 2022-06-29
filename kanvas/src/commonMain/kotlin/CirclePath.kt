package com.juul.krayon.kanvas

/** Returns a [Path] from two arcs that matches the circle with [centerX], [centerY], and [radius]. */
public fun circlePath(centerX: Float, centerY: Float, radius: Float): Path = Path {
    val left = centerX - radius
    val top = centerY - radius
    val right = centerX + radius
    val bottom = centerY + radius
    arcTo(left, top, right, bottom, 0f, 180f, forceMoveTo = true)
    arcTo(left, top, right, bottom, 180f, 180f, forceMoveTo = false)
}
