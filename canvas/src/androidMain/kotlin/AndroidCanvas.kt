package com.juul.krayon.canvas

import android.graphics.Region
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

/** Implementation of [Canvas] which wraps a native Android [Canvas][android.graphics.Canvas]. */
public class AndroidCanvas(
    private val androidCanvas: android.graphics.Canvas
) : Canvas<AndroidPaint, AndroidPath> {

    override val width: Float
        get() = androidCanvas.width.toFloat()

    override val height: Float
        get() = androidCanvas.height.toFloat()

    override fun buildPaint(paint: Paint): AndroidPaint = paint.toAndroid()

    override fun buildPath(actions: PathBuilder<*>.() -> Unit): AndroidPath =
        AndroidPathBuilder().apply(actions).build()

    override fun drawArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        useCenter: Boolean,
        paint: AndroidPaint
    ) {
        androidCanvas.drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: AndroidPaint) {
        androidCanvas.drawCircle(centerX, centerY, radius, paint)
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: AndroidPaint) {
        androidCanvas.drawLine(startX, startY, endX, endY, paint)
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: AndroidPaint) {
        androidCanvas.drawOval(left, top, right, bottom, paint)
    }

    override fun drawPath(path: AndroidPath, paint: AndroidPaint) {
        androidCanvas.drawPath(path, paint)
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: AndroidPaint) {
        androidCanvas.drawText(text, 0, text.length, x, y, paint)
    }

    @Suppress("DEPRECATION")
    override fun pushClip(clip: Clip<AndroidPath>) {
        androidCanvas.save()
        when (clip) {
            is Clip.Rect -> when (clip.operation) {
                Clip.Operation.Intersection ->
                    androidCanvas.clipRect(clip.left, clip.top, clip.right, clip.bottom)
                Clip.Operation.Difference ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        androidCanvas.clipRect(clip.left, clip.top, clip.right, clip.bottom, Region.Op.DIFFERENCE)
                    } else {
                        androidCanvas.clipOutRect(clip.left, clip.top, clip.right, clip.bottom)
                    }
            }
            is Clip.Path -> when (clip.operation) {
                Clip.Operation.Intersection ->
                    androidCanvas.clipPath(clip.path)
                Clip.Operation.Difference ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        androidCanvas.clipPath(clip.path, Region.Op.DIFFERENCE)
                    } else {
                        androidCanvas.clipOutPath(clip.path)
                    }
            }
        }
    }

    override fun pushTransform(transform: Transform) {
        // Even though a transform should usually be either a single item or a list, use recursion
        // because it's possible to make a tree with [Transform.InOrder] when users are sadistic.
        fun applyTransform(transform: Transform) {
            when (transform) {
                is Transform.InOrder ->
                    transform.transformations.forEach(::applyTransform)
                is Transform.Scale ->
                    if (transform.pivotX == 0f && transform.pivotY == 0f) {
                        androidCanvas.scale(transform.horizontal, transform.vertical)
                    } else {
                        androidCanvas.scale(transform.horizontal, transform.vertical, transform.pivotX, transform.pivotY)
                    }
                is Transform.Rotate ->
                    if (transform.pivotX == 0f && transform.pivotY == 0f) {
                        androidCanvas.rotate(transform.degrees)
                    } else {
                        androidCanvas.rotate(transform.degrees, transform.pivotX, transform.pivotY)
                    }
                is Transform.Translate ->
                    androidCanvas.translate(transform.horizontal, transform.vertical)
                is Transform.Skew ->
                    androidCanvas.skew(transform.horizontal, transform.vertical)
            }
        }
        androidCanvas.save()
        applyTransform(transform)
    }

    override fun pop() {
        androidCanvas.restore()
    }
}

/**
 * Transforms a [Canvas] with a [Transform.Scale] such that 1 unit is equal to 1dp. Unlike [withTransform],
 * calls to [Canvas.width] and [Canvas.height] inside this space are adjusted to account for the scale.
 */
inline fun <PAINT, PATH> Canvas<PAINT, PATH>.withDpScale(
    displayMetrics: DisplayMetrics,
    block: Canvas<PAINT, PATH>.() -> Unit
) {
    val scale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, displayMetrics)
    val width = this.width / scale
    val height = this.height / scale
    withTransform(Transform.Scale(horizontal = scale, vertical = scale)) {
        val receiver = object : Canvas<PAINT, PATH> by this {
            override val width: Float get() = width
            override val height: Float get() = height
        }
        with(receiver) { block() }
    }
}
