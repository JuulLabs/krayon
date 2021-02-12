package com.juul.krayon.kanvas

import android.graphics.Canvas
import android.graphics.Region
import android.os.Build
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

/** Implementation of [Kanvas] which wraps a native Android [Canvas]. */
public class AndroidKanvas(
    private val sourceCanvas: Canvas,
) : Kanvas<AndroidPaint, AndroidPath> {

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
        sourceCanvas.drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: AndroidPaint) {
        sourceCanvas.drawCircle(centerX, centerY, radius, paint)
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: AndroidPaint) {
        sourceCanvas.drawLine(startX, startY, endX, endY, paint)
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: AndroidPaint) {
        sourceCanvas.drawOval(left, top, right, bottom, paint)
    }

    override fun drawPath(path: AndroidPath, paint: AndroidPaint) {
        sourceCanvas.drawPath(path, paint)
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: AndroidPaint) {
        sourceCanvas.drawText(text, 0, text.length, x, y, paint)
    }

    @Suppress("DEPRECATION")
    override fun pushClip(clip: Clip<AndroidPath>) {
        sourceCanvas.save()
        when (clip) {
            is Clip.Rect -> when (clip.operation) {
                Clip.Operation.Intersection ->
                    sourceCanvas.clipRect(clip.left, clip.top, clip.right, clip.bottom)
                Clip.Operation.Difference ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        sourceCanvas.clipRect(clip.left, clip.top, clip.right, clip.bottom, Region.Op.DIFFERENCE)
                    } else {
                        sourceCanvas.clipOutRect(clip.left, clip.top, clip.right, clip.bottom)
                    }
            }
            is Clip.Path -> when (clip.operation) {
                Clip.Operation.Intersection ->
                    sourceCanvas.clipPath(clip.path)
                Clip.Operation.Difference ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        sourceCanvas.clipPath(clip.path, Region.Op.DIFFERENCE)
                    } else {
                        sourceCanvas.clipOutPath(clip.path)
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
                        sourceCanvas.scale(transform.horizontal, transform.vertical)
                    } else {
                        sourceCanvas.scale(transform.horizontal, transform.vertical, transform.pivotX, transform.pivotY)
                    }
                is Transform.Rotate ->
                    if (transform.pivotX == 0f && transform.pivotY == 0f) {
                        sourceCanvas.rotate(transform.degrees)
                    } else {
                        sourceCanvas.rotate(transform.degrees, transform.pivotX, transform.pivotY)
                    }
                is Transform.Translate ->
                    sourceCanvas.translate(transform.horizontal, transform.vertical)
                is Transform.Skew ->
                    sourceCanvas.skew(transform.horizontal, transform.vertical)
            }
        }
        sourceCanvas.save()
        applyTransform(transform)
    }

    override fun pop() {
        sourceCanvas.restore()
    }
}
