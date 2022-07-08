package com.juul.krayon.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.scale
import com.juul.krayon.color.Color
import com.juul.krayon.kanvas.Clip
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.build
import androidx.compose.ui.graphics.Path as ComposePath

public class ComposeKanvas internal constructor(
    internal val scope: DrawScope,
    internal val resourceCache: ResourceCache,
) : Kanvas<ComposePath> {

    override val width: Float = scope.size.width / scope.density
    override val height: Float = scope.size.height / scope.density

    init {
        scope.drawContext.transform.scale(scope.density, pivot = Offset.Zero)
    }

    private inline fun withBrushAndStyle(
        paint: Paint,
        crossinline action: DrawScope.(Brush, DrawStyle) -> Unit,
    ) = with(scope) {
        when (paint) {
            is Paint.FillAndStroke -> {
                action(paint.fill.toBrush(), paint.fill.drawStyle)
                action(paint.stroke.toBrush(), paint.stroke.drawStyle)
            }
            is Paint.GradientAndStroke -> {
                action(paint.gradient.toBrush(), paint.gradient.drawStyle)
                action(paint.stroke.toBrush(), paint.stroke.drawStyle)
            }
            else -> action(paint.toBrush(), paint.drawStyle)
        }
    }

    override fun buildPath(actions: Path): ComposePath = ComposePathBuilder().build(actions)

    override fun drawArc(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, paint: Paint) {
        withBrushAndStyle(paint) { brush, style ->
            drawArc(brush, startAngle, sweepAngle, useCenter = false, topLeft = Offset(left, top), Size(right - left, bottom - top), style = style)
        }
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: Paint) {
        withBrushAndStyle(paint) { brush, style ->
            drawCircle(brush, radius, Offset(centerX, centerY), style = style)
        }
    }

    override fun drawColor(color: Color) {
        scope.drawRect(color.asComposeColor())
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint) {
        val stroke = requireNotNull(paint.strokeOrNull) { "Paint must contain a stroke to perform drawLine." }
        scope.drawLine(
            brush = stroke.toBrush(),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = stroke.width,
            cap = stroke.composeCap,
            pathEffect = stroke.composePathEffect,
        )
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        withBrushAndStyle(paint) { brush, style ->
            drawOval(brush, topLeft = Offset(left, top), Size(right - left, bottom - top), style = style)
        }
    }

    override fun drawPath(path: ComposePath, paint: Paint) {
        withBrushAndStyle(paint) { brush, style ->
            drawPath(path, brush, style = style)
        }
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        withBrushAndStyle(paint) { brush, style ->
            drawRect(brush, topLeft = Offset(left, top), Size(right - left, bottom - top), style = style)
        }
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: Paint) {
        require(paint is Paint.Text)
        // Delegate to expect/actual function
        drawText(this, text, x, y, paint)
    }

    override fun pushClip(clip: Clip<ComposePath>) {
        scope.drawContext.canvas.save()
        when (clip) {
            is Clip.Rect -> scope.drawContext.transform.clipRect(clip.left, clip.top, clip.right, clip.bottom)
            is Clip.Path -> scope.drawContext.transform.clipPath(clip.path)
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
                    scope.drawContext.transform.scale(transform.horizontal, transform.vertical, pivot = Offset(transform.pivotX, transform.pivotY))
                is Transform.Rotate ->
                    scope.drawContext.transform.rotate(transform.degrees, pivot = Offset(transform.pivotX, transform.pivotY))
                is Transform.Translate ->
                    scope.drawContext.transform.translate(transform.horizontal, transform.vertical)
                is Transform.Skew ->
                    TODO("Skew transforms not yet implemented.")
            }
        }
        scope.drawContext.canvas.save()
        applyTransform(transform)
    }

    override fun pop() {
        scope.drawContext.canvas.restore()
    }
}
