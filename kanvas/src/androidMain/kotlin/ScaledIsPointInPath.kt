package com.juul.krayon.kanvas

import android.graphics.Matrix

public class ScaledIsPointInPath(
    private val scalingFactor: Float = 1f
) : IsPointInPath {

    override fun isPointInPath(transform: Transform, path: Path, x: Float, y: Float): Boolean {
        val nativePath = path.toAndroid().apply {
            val matrix = Matrix().applyTransform(transform)
            transform(matrix)
        }
        val left = x / scalingFactor - 0.002f
        val top = y / scalingFactor - 0.002f
        val right = x / scalingFactor + 0.002f
        val bottom = y / scalingFactor + 0.002f
        val touchPath = android.graphics.Path().apply { addRect(left, top, right, bottom, android.graphics.Path.Direction.CW) }
        nativePath.op(touchPath, android.graphics.Path.Op.INTERSECT)
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
