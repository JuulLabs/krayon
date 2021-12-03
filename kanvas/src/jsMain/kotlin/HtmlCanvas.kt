package com.juul.krayon.kanvas

import com.juul.krayon.color.Color
import org.w3c.dom.BEVEL
import org.w3c.dom.BUTT
import org.w3c.dom.CENTER
import org.w3c.dom.CanvasLineCap
import org.w3c.dom.CanvasLineJoin
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.LEFT
import org.w3c.dom.MITER
import org.w3c.dom.Path2D
import org.w3c.dom.RIGHT
import org.w3c.dom.ROUND
import org.w3c.dom.SQUARE
import kotlin.math.PI

/** Helper for the common case of deciding if [CanvasRenderingContext2D] should call a `fill` function or not. */
private fun shouldFill(paint: Paint): Boolean =
    paint is Paint.Fill || paint is Paint.FillAndStroke

/** Helper for the common case of deciding if [CanvasRenderingContext2D] should call a `stroke` function or not. */
private fun shouldStroke(paint: Paint): Boolean =
    paint is Paint.Stroke || paint is Paint.FillAndStroke

public class HtmlCanvas(
    element: HTMLCanvasElement,
) : Kanvas<Path2D> {

    private val context = element.getContext("2d") as CanvasRenderingContext2D

    override val width: Float
        get() = context.canvas.width.toFloat()

    override val height: Float
        get() = context.canvas.height.toFloat()

    override fun buildPath(actions: PathBuilder<*>.() -> Unit): Path2D =
        Path2DBuilder().apply(actions).build()

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
        applyStyle(paint)
        context.beginPath()
        val radiusX = (right - left) / 2.0
        val radiusY = (bottom - top) / 2.0
        val x = left + radiusX
        val y = top + radiusY
        val startAngleRadians = startAngle * PI / 180f
        val endAngleRadians = (startAngle + sweepAngle) * PI / 180f
        context.ellipse(x, y, radiusX, radiusY, rotation = 0.0, startAngleRadians, endAngleRadians)
        if (shouldFill(paint)) context.fill()
        if (shouldStroke(paint)) context.stroke()
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: Paint) {
        require(paint !is Paint.Text)
        applyStyle(paint)
        context.beginPath()
        context.arc(centerX.toDouble(), centerY.toDouble(), radius.toDouble(), 0.0, 2 * PI)
        if (shouldFill(paint)) context.fill()
        if (shouldStroke(paint)) context.stroke()
    }

    override fun drawColor(color: Color) {
        drawRect(0f, 0f, width, height, Paint.Fill(color))
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint) {
        require(paint !is Paint.Text)
        applyStyle(paint)
        context.beginPath()
        context.moveTo(startX.toDouble(), startY.toDouble())
        context.lineTo(endX.toDouble(), endY.toDouble())
        if (shouldFill(paint)) context.fill()
        if (shouldStroke(paint)) context.stroke()
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        drawArc(left, top, right, bottom, 0f, 360f, paint)
    }

    override fun drawPath(path: Path2D, paint: Paint) {
        require(paint !is Paint.Text)
        applyStyle(paint)
        context.beginPath() // TODO: Look up if this is necessary.
        if (shouldFill(paint)) context.fill()
        if (shouldStroke(paint)) context.stroke()
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        require(paint !is Paint.Text)
        applyStyle(paint)
        val width = right.toDouble() - left
        val height = bottom.toDouble() - top
        if (shouldFill(paint)) context.fillRect(left.toDouble(), top.toDouble(), width, height)
        if (shouldStroke(paint)) context.strokeRect(left.toDouble(), top.toDouble(), width, height)
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: Paint) {
        require(paint is Paint.Text)
        applyStyle(paint)
        context.fillText(text.toString(), x.toDouble(), y.toDouble())
    }

    override fun pushClip(clip: Clip<Path2D>) {
        context.save()
        context.beginPath()
        when (clip) {
            is Clip.Rect -> {
                context.rect(
                    clip.left.toDouble(),
                    clip.top.toDouble(),
                    clip.right.toDouble() - clip.left,
                    clip.bottom.toDouble() - clip.top
                )
                context.clip()
            }
            is Clip.Path -> {
                context.clip(clip.path)
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
                is Transform.Scale -> {
                    context.translate(transform.pivotX.toDouble(), transform.pivotY.toDouble())
                    context.scale(transform.horizontal.toDouble(), transform.vertical.toDouble())
                    context.translate(-transform.pivotX.toDouble(), -transform.pivotY.toDouble())
                }
                is Transform.Rotate -> {
                    context.translate(transform.pivotX.toDouble(), transform.pivotY.toDouble())
                    context.rotate(transform.degrees * PI / 180.0)
                    context.translate(-transform.pivotX.toDouble(), -transform.pivotY.toDouble())
                }
                is Transform.Translate -> {
                    context.translate(transform.horizontal.toDouble(), transform.vertical.toDouble())
                }
                is Transform.Skew -> {
                    // Translated from MSDN: https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/transform
                    // The transformation matrix is described by:
                    //     [ a c e ]
                    //     [ b d f ]
                    //     [ 0 0 1 ]
                    // where:
                    //     a - Horizontal scaling. A value of 1 results in no scaling.
                    //     b - Vertical skewing
                    //     c - Horizontal skewing
                    //     d - Vertical scaling. A value of 1 results in no scaling.
                    //     e - Horizontal translation (moving)
                    //     f - Vertical translation (moving)
                    context.transform(
                        a = 1.0,
                        b = transform.vertical.toDouble(),
                        c = transform.horizontal.toDouble(),
                        d = 1.0,
                        e = 0.0,
                        f = 0.0
                    )
                }
            }
        }
        context.save()
        applyTransform(transform)
    }

    override fun pop() {
        context.restore()
    }

    /** Sets the [context]'s render style to match [paint] via delegation to strongly typed overloads. */
    private fun applyStyle(paint: Paint) {
        when (paint) {
            is Paint.Fill -> applyStyle(paint)
            is Paint.Stroke -> applyStyle(paint)
            is Paint.FillAndStroke -> {
                applyStyle(paint.fill)
                applyStyle(paint.stroke)
            }
            is Paint.Text -> applyStyle(paint)
        }
    }

    private fun applyStyle(paint: Paint.Fill) {
        context.fillStyle = paint.color.toHexString()
    }

    private fun applyStyle(paint: Paint.Stroke) {
        context.strokeStyle = paint.color.toHexString()
        context.lineWidth = paint.width.toDouble()
        context.lineCap = when (paint.cap) {
            Paint.Stroke.Cap.Butt -> CanvasLineCap.BUTT
            Paint.Stroke.Cap.Round -> CanvasLineCap.ROUND
            Paint.Stroke.Cap.Square -> CanvasLineCap.SQUARE
        }
        context.lineJoin = when (paint.join) {
            Paint.Stroke.Join.Bevel -> CanvasLineJoin.BEVEL
            Paint.Stroke.Join.Round -> CanvasLineJoin.ROUND
            is Paint.Stroke.Join.Miter -> {
                context.miterLimit = paint.join.limit.toDouble()
                CanvasLineJoin.MITER
            }
        }
        context.setLineDash(
            when (paint.dash) {
                Paint.Stroke.Dash.None -> emptyArray()
                is Paint.Stroke.Dash.Pattern -> Array(paint.dash.intervals.size) { index ->
                    paint.dash.intervals[index].toDouble()
                }
            }
        )
    }

    private fun applyStyle(paint: Paint.Text) {
        context.fillStyle = paint.color.toHexString()
        context.textAlign = when (paint.alignment) {
            Paint.Text.Alignment.Left -> CanvasTextAlign.LEFT
            Paint.Text.Alignment.Center -> CanvasTextAlign.CENTER
            Paint.Text.Alignment.Right -> CanvasTextAlign.RIGHT
        }
        context.font = "${paint.size}px ${paint.font.names.joinToString { "\"$it\"" }}"
    }
}
