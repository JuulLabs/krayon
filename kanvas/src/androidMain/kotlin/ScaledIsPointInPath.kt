package com.juul.krayon.kanvas

import android.graphics.Matrix
import android.graphics.Path.Direction
import android.graphics.Path.Op.INTERSECT

/** Distance from the center point to the sides of the touch bounding box. */
private const val HALF_RECT_SIZE = 0.002f

/**
 * Unlike other platforms, Android doesn't provide an actual way of checking if a point is contained
 * in a path. Android does, however, allow you to intersect paths. As such, check if a very small
 * rectangle around the actual touch point overlaps with the path.
 *
 * This class also builds in a `scalingFactor` which is used for dealing with screen density.
 */
public class ScaledIsPointInPath(
    private val scalingFactor: Float = 1f,
) : IsPointInPath {

    override fun isPointInPath(transform: Transform, path: Path, x: Float, y: Float): Boolean {
        val nativePath = path.toAndroid().apply {
            val matrix = Matrix().applyTransform(transform)
            transform(matrix)
        }
        val left = x / scalingFactor - HALF_RECT_SIZE
        val top = y / scalingFactor - HALF_RECT_SIZE
        val right = x / scalingFactor + HALF_RECT_SIZE
        val bottom = y / scalingFactor + HALF_RECT_SIZE
        val touchPath = android.graphics.Path().apply { addRect(left, top, right, bottom, Direction.CW) }
        nativePath.op(touchPath, INTERSECT)
        return !nativePath.isEmpty
    }
}

private fun Matrix.applyTransform(transform: Transform): Matrix = apply {
    when (transform) {
        is Transform.InOrder ->
            transform.transformations.forEach(this::applyTransform)
        is Transform.Scale ->
            if (transform.pivotX == 0f && transform.pivotY == 0f) {
                postScale(transform.horizontal, transform.vertical)
            } else {
                postScale(transform.horizontal, transform.vertical, transform.pivotX, transform.pivotY)
            }
        is Transform.Rotate ->
            if (transform.pivotX == 0f && transform.pivotY == 0f) {
                postRotate(transform.degrees)
            } else {
                postRotate(transform.degrees, transform.pivotX, transform.pivotY)
            }
        is Transform.Translate ->
            postTranslate(transform.horizontal, transform.vertical)
        is Transform.Skew ->
            postSkew(transform.horizontal, transform.vertical)
    }
}
