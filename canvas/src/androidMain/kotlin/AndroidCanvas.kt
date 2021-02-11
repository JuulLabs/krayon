package com.juul.krayon.canvas

import android.graphics.Region
import android.os.Build
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

/** Create an [AndroidCanvas]. */
public fun AndroidCanvas(
    sourceCanvas: android.graphics.Canvas,
    scalingFactor: Float = 1f,
): AndroidCanvas = AndroidCanvas(sourceCanvas, scalingFactor)

/**
 * Implementation of [Canvas] which wraps a native Android [Canvas][android.graphics.Canvas].
 *
 * It may be pre-scaled by a [scalingFactor]. If it is, [width] and [height] are adjusted to
 * account for this scaling factor.
 */
public class AndroidCanvas internal constructor(
    private var androidCanvas: android.graphics.Canvas?,
    private val scalingFactor: Float = 1f,
) : Canvas<AndroidPaint, AndroidPath> {

    private val preTransform = Transform.Scale(horizontal = scalingFactor, vertical = scalingFactor)

    override val width: Float
        get() = requireCanvas().width / scalingFactor

    override val height: Float
        get() = requireCanvas().height / scalingFactor

    init {
        if (androidCanvas != null) {
            pushTransform(preTransform)
        }
    }

    override fun buildPaint(paint: Paint): AndroidPaint = paint.toAndroid()

    override fun buildPath(actions: PathBuilder<*>.() -> Unit): AndroidPath =
        AndroidPathBuilder().apply(actions).build()

    private fun requireCanvas(): android.graphics.Canvas = checkNotNull(androidCanvas)

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
        requireCanvas().drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: AndroidPaint) {
        requireCanvas().drawCircle(centerX, centerY, radius, paint)
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: AndroidPaint) {
        requireCanvas().drawLine(startX, startY, endX, endY, paint)
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: AndroidPaint) {
        requireCanvas().drawOval(left, top, right, bottom, paint)
    }

    override fun drawPath(path: AndroidPath, paint: AndroidPaint) {
        requireCanvas().drawPath(path, paint)
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: AndroidPaint) {
        requireCanvas().drawText(text, 0, text.length, x, y, paint)
    }

    @Suppress("DEPRECATION")
    override fun pushClip(clip: Clip<AndroidPath>) {
        requireCanvas().save()
        when (clip) {
            is Clip.Rect -> when (clip.operation) {
                Clip.Operation.Intersection ->
                    requireCanvas().clipRect(clip.left, clip.top, clip.right, clip.bottom)
                Clip.Operation.Difference ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        requireCanvas().clipRect(clip.left, clip.top, clip.right, clip.bottom, Region.Op.DIFFERENCE)
                    } else {
                        requireCanvas().clipOutRect(clip.left, clip.top, clip.right, clip.bottom)
                    }
            }
            is Clip.Path -> when (clip.operation) {
                Clip.Operation.Intersection ->
                    requireCanvas().clipPath(clip.path)
                Clip.Operation.Difference ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        requireCanvas().clipPath(clip.path, Region.Op.DIFFERENCE)
                    } else {
                        requireCanvas().clipOutPath(clip.path)
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
                        requireCanvas().scale(transform.horizontal, transform.vertical)
                    } else {
                        requireCanvas().scale(transform.horizontal, transform.vertical, transform.pivotX, transform.pivotY)
                    }
                is Transform.Rotate ->
                    if (transform.pivotX == 0f && transform.pivotY == 0f) {
                        requireCanvas().rotate(transform.degrees)
                    } else {
                        requireCanvas().rotate(transform.degrees, transform.pivotX, transform.pivotY)
                    }
                is Transform.Translate ->
                    requireCanvas().translate(transform.horizontal, transform.vertical)
                is Transform.Skew ->
                    requireCanvas().skew(transform.horizontal, transform.vertical)
            }
        }
        requireCanvas().save()
        applyTransform(transform)
    }

    override fun pop() {
        requireCanvas().restore()
    }

    /** Mutability on this class is to power [CanvasView] without allocations. Otherwise we should consider this immutable. */
    internal fun setCanvas(androidCanvas: android.graphics.Canvas?) {
        if (this.androidCanvas != null) {
            pop()
        }
        this.androidCanvas = androidCanvas
        if (androidCanvas != null) {
            pushTransform(preTransform)
        }
    }
}
