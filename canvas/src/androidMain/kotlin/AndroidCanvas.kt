package com.juul.krayon.canvas

import android.graphics.Region
import android.os.Build
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

/**
 * Implementation of [Canvas] which wraps a native Android [Canvas][android.graphics.Canvas].
 *
 * It may be pre-scaled by a [scalingFactor]. If it is, [width] and [height] are adjusted to
 * account for this scaling factor.
 *
 * Note that the backing canvas for this object is mutable, and can be replaced with [setCanvas].
 * This is provided as an optimization for cases where allocation should be strongly avoided,
 * such as for [CanvasView].
 */
public class AndroidCanvas(
    private var androidCanvas: android.graphics.Canvas?,
    private val scalingFactor: Float = 1f,
) : Canvas<AndroidPaint, AndroidPath> {

    private val preTransform = Transform.Scale(horizontal = scalingFactor, vertical = scalingFactor)

    override val width: Float
        get() = androidCanvas?.width?.toFloat() ?: 0 / scalingFactor

    override val height: Float
        get() = androidCanvas?.height?.toFloat() ?: 0 / scalingFactor

    init {
        pushTransform(preTransform)
    }

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
        paint: AndroidPaint,
    ) {
        androidCanvas?.drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: AndroidPaint) {
        androidCanvas?.drawCircle(centerX, centerY, radius, paint)
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: AndroidPaint) {
        androidCanvas?.drawLine(startX, startY, endX, endY, paint)
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: AndroidPaint) {
        androidCanvas?.drawOval(left, top, right, bottom, paint)
    }

    override fun drawPath(path: AndroidPath, paint: AndroidPaint) {
        androidCanvas?.drawPath(path, paint)
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: AndroidPaint) {
        androidCanvas?.drawText(text, 0, text.length, x, y, paint)
    }

    @Suppress("DEPRECATION")
    override fun pushClip(clip: Clip<AndroidPath>) {
        androidCanvas?.save()
        when (clip) {
            is Clip.Rect -> when (clip.operation) {
                Clip.Operation.Intersection ->
                    androidCanvas?.clipRect(clip.left, clip.top, clip.right, clip.bottom)
                Clip.Operation.Difference ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        androidCanvas?.clipRect(clip.left, clip.top, clip.right, clip.bottom, Region.Op.DIFFERENCE)
                    } else {
                        androidCanvas?.clipOutRect(clip.left, clip.top, clip.right, clip.bottom)
                    }
            }
            is Clip.Path -> when (clip.operation) {
                Clip.Operation.Intersection ->
                    androidCanvas?.clipPath(clip.path)
                Clip.Operation.Difference ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        androidCanvas?.clipPath(clip.path, Region.Op.DIFFERENCE)
                    } else {
                        androidCanvas?.clipOutPath(clip.path)
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
                        androidCanvas?.scale(transform.horizontal, transform.vertical)
                    } else {
                        androidCanvas?.scale(transform.horizontal, transform.vertical, transform.pivotX, transform.pivotY)
                    }
                is Transform.Rotate ->
                    if (transform.pivotX == 0f && transform.pivotY == 0f) {
                        androidCanvas?.rotate(transform.degrees)
                    } else {
                        androidCanvas?.rotate(transform.degrees, transform.pivotX, transform.pivotY)
                    }
                is Transform.Translate ->
                    androidCanvas?.translate(transform.horizontal, transform.vertical)
                is Transform.Skew ->
                    androidCanvas?.skew(transform.horizontal, transform.vertical)
            }
        }
        androidCanvas?.save()
        applyTransform(transform)
    }

    override fun pop() {
        androidCanvas?.restore()
    }

    public fun setCanvas(androidCanvas: android.graphics.Canvas?) {
        this.androidCanvas = androidCanvas
        pushTransform(preTransform)
    }
}
