package com.juul.krayon.kanvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import com.juul.krayon.color.Color
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

/** Create an [AndroidKanvas]. */
public fun AndroidKanvas(
    context: Context,
    canvas: Canvas,
    scalingFactor: Float = 1f,
    paintCache: PaintCache = PaintCache(context),
): AndroidKanvas = AndroidKanvas(context, canvas as Canvas?, scalingFactor, paintCache)

/**
 * Implementation of [Kanvas] which wraps a native Android [Canvas].
 *
 * It may be pre-scaled by a [scalingFactor]. If it is, [width] and [height] are adjusted to
 * account for this scaling factor.
 */
public class AndroidKanvas internal constructor(
    private val context: Context,
    private var canvas: Canvas?,
    private val scalingFactor: Float = 1f,
    private val paintCache: PaintCache = PaintCache(context),
) : Kanvas<AndroidPath> {

    private val preTransform = Transform.Scale(horizontal = scalingFactor, vertical = scalingFactor)

    /** On API 19 & 20, [drawArc] and [drawOval] must use [RectF]. */
    private val compatRectF = RectF()

    override val width: Float
        get() = requireCanvas().width / scalingFactor

    override val height: Float
        get() = requireCanvas().height / scalingFactor

    init {
        if (canvas != null) {
            pushTransform(preTransform)
        }
    }

    override fun buildPath(actions: Path): AndroidPath =
        AndroidPathBuilder().build(actions)

    private fun requireCanvas(): Canvas = checkNotNull(canvas)

    private inline fun withPaint(
        paint: Paint,
        crossinline action: Canvas.(AndroidPaint) -> Unit,
    ) = with(requireCanvas()) {
        if (paint is Paint.FillAndStroke) {
            action(paintCache[paint.fill])
            action(paintCache[paint.stroke])
        } else {
            action(paintCache[paint])
        }
    }

    override fun drawArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        paint: Paint,
    ) {
        require(paint !is Paint.Text)
        compatRectF.set(left, top, right, bottom)
        withPaint(paint) { drawArc(compatRectF, startAngle, sweepAngle, false, it) }
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: Paint) {
        require(paint !is Paint.Text)
        withPaint(paint) { drawCircle(centerX, centerY, radius, it) }
    }

    override fun drawColor(color: Color) {
        requireCanvas().drawColor(color.argb)
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint) {
        require(paint !is Paint.Text)
        withPaint(paint) { drawLine(startX, startY, endX, endY, it) }
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        require(paint !is Paint.Text)
        compatRectF.set(left, top, right, bottom)
        withPaint(paint) { drawOval(compatRectF, it) }
    }

    override fun drawPath(path: AndroidPath, paint: Paint) {
        require(paint !is Paint.Text)
        withPaint(paint) { drawPath(path, it) }
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        require(paint !is Paint.Text)
        withPaint(paint) { drawRect(left, top, right, bottom, it) }
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: Paint) {
        require(paint is Paint.Text)
        withPaint(paint) { drawText(text, 0, text.length, x, y, it) }
    }

    @Suppress("DEPRECATION")
    override fun pushClip(clip: Clip<AndroidPath>) {
        requireCanvas().save()
        when (clip) {
            is Clip.Rect -> requireCanvas().clipRect(clip.left, clip.top, clip.right, clip.bottom)
            is Clip.Path -> requireCanvas().clipPath(clip.path)
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
    internal fun setCanvas(canvas: Canvas?) {
        if (this.canvas != null) {
            pop()
        }
        this.canvas = canvas
        if (canvas != null) {
            pushTransform(preTransform)
        }
    }
}
