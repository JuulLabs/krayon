package com.juul.krayon.canvas

import android.graphics.Region
import android.os.Build
import java.util.WeakHashMap

/** Implementation of [Canvas] with wraps a native Android [Canvas][android.graphics.Canvas]. */
public class AndroidCanvas(
    private val androidCanvas: android.graphics.Canvas
) : Canvas<AndroidPath> {

    /** Cache of [Paint] to [AndroidPaint] to avoid re-allocating excessively when the same paint is re-used. */
    private val androidPaints = WeakHashMap<Paint, android.graphics.Paint>()

    /** A caching version of [Paint.toAndroid] to avoid allocation on repeated uses. */
    private fun Paint.asAndroid() = androidPaints.getOrPut(this) { this.toAndroid() }

    override fun drawArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        useCenter: Boolean,
        paint: Paint
    ) {
        androidCanvas.drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint.asAndroid())
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: Paint) {
        androidCanvas.drawCircle(centerX, centerY, radius, paint.asAndroid())
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint.Stroke) {
        androidCanvas.drawLine(startX, startY, endX, endY, paint.asAndroid())
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        androidCanvas.drawOval(left, top, right, bottom, paint.asAndroid())
    }

    override fun buildPath(actions: Path.Builder<*>.() -> Unit): AndroidPath =
        AndroidPath.Builder().apply(actions).build()

    override fun drawPath(path: AndroidPath, paint: Paint) {
        androidCanvas.drawPath(path.data, paint.asAndroid())
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: Paint.Text) {
        androidCanvas.drawText(text, 0, text.length, x, y, paint.asAndroid())
    }

    @Suppress("DEPRECATION")
    override fun pushClip(clip: Clip) {
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
            is Clip.Path<*> -> {
                // FIXME: There is probably a way to make this work without runtime checks, but
                //        anything I came up with made the API a lot worse.
                require(clip.path is AndroidPath) {
                    "AndroidCanvas cannot use Clip.Path<${clip.path::class.java.name}>. Expected Clip.Path<AndroidPath>."
                }
                val path = clip.path.data
                when (clip.operation) {
                    Clip.Operation.Intersection ->
                        androidCanvas.clipPath(path)
                    Clip.Operation.Difference ->
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            androidCanvas.clipPath(path, Region.Op.DIFFERENCE)
                        } else {
                            androidCanvas.clipOutPath(path)
                        }
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
