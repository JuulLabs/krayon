package com.juul.krayon.kanvas

import com.juul.krayon.color.Color
import platform.CoreGraphics.CGColorCreateSRGB
import platform.CoreGraphics.CGColorRef
import platform.CoreGraphics.CGPathCreateWithRect
import platform.CoreGraphics.CGPathRef
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetWidth
import platform.CoreGraphics.CGRectMake
import platform.QuartzCore.CAGradientLayer
import platform.QuartzCore.CALayer
import platform.QuartzCore.CAShapeLayer

public class CALayerKanvas(
    private val root: CALayer,
) : Kanvas<CGPathRef> {

    private val layers: MutableList<CALayer> = mutableListOf()
    private val layer: CALayer get() = layers.lastOrNull() ?: root

    override val width: Float get() = CGRectGetWidth(root.bounds).toFloat()
    override val height: Float get() = CGRectGetHeight(root.bounds).toFloat()

    override fun buildPath(actions: Path): CGPathRef = actions.toCGPath()

    override fun drawArc(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, paint: Paint) {
        val path = buildPath { arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo = true) }
        drawPath(path, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: Paint) {
        val path = buildPath(circlePath(centerX, centerY, radius))
        drawPath(path, paint)
    }

    override fun drawColor(color: Color) {
        val sublayer = CALayer().apply { backgroundColor = color.toCGColor() }
        layer.addSublayer(sublayer)
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint) {
        val path = buildPath {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        drawPath(path, paint)
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        val path = buildPath {
            arcTo(left, top, right, bottom, 0f, 180f, forceMoveTo = true)
            arcTo(left, top, right, bottom, 180f, 180f, forceMoveTo = false)
        }
        drawPath(path, paint)
    }

    override fun drawPath(path: CGPathRef, paint: Paint) {
        if (paint is Paint.Text) {
            error("Cannot draw path using text paint.")
        } else if (paint is Paint.GradientAndStroke) {
            // Gradient cannot be rendered on the same layer as stroke. Use recursion to draw
            // the two layers separately.
            drawPath(path, paint.gradient)
            drawPath(path, paint.stroke)
        } else if (paint is Paint.Gradient) {
            // Gradients work differently than simple fill/stroke. Instead of applying drawing info
            // to the shape layer directly, we define a full layer for gradients and mask it.
            val gradientLayer = CAGradientLayer().apply {
                // TODO: Actually add the gradient color information
                mask = CAShapeLayer().apply { this.path = path }
            }
            layer.addSublayer(gradientLayer)
        } else {
            // Fill/stroke can be set directly on a CAShapeLayer, so set them here.
            val sublayer = CAShapeLayer().apply { this.path = path }
            when (paint) {
                is Paint.Fill -> paint.applyTo(sublayer)
                is Paint.Stroke -> paint.applyTo(sublayer)
                is Paint.FillAndStroke -> {
                    paint.fill.applyTo(sublayer)
                    paint.stroke.applyTo(sublayer)
                }
                else -> error("Unreachable")
            }
            layer.addSublayer(sublayer)
        }
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        val path = pathFromRect(left, top, right, bottom)
        drawPath(path, paint)
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: Paint) {
        TODO("Not yet implemented")
    }

    override fun pushClip(clip: Clip<CGPathRef>) {
        val sublayer = CALayer().apply {
            mask = CAShapeLayer().apply {
                path = when (clip) {
                    is Clip.Path -> clip.path
                    is Clip.Rect -> pathFromRect(clip.left, clip.top, clip.right, clip.bottom)
                    else -> error("Unknown clip type.")
                }
            }
        }
        layer.addSublayer(sublayer)
        layers.add(sublayer)
    }

    override fun pushTransform(transform: Transform) {
        TODO("Not yet implemented")
    }

    override fun pop() {
        check(layers.isNotEmpty()) { "Cannot pop from empty layers." }
        layers.removeLast()
    }
}

private fun Paint.Fill.applyTo(layer: CAShapeLayer) {
    layer.fillColor = color.toCGColor()
}

private fun Paint.Stroke.applyTo(layer: CAShapeLayer) {
    layer.strokeColor = color.toCGColor()
    layer.lineWidth = width.toDouble()
}

private fun Color.toCGColor(): CGColorRef = CGColorCreateSRGB(
    red = red / 255.0,
    green = green / 255.0,
    blue = blue / 255.0,
    alpha = alpha / 255.0
)!!

internal fun pathFromRect(left: Float, top: Float, right: Float, bottom: Float): CGPathRef {
    val rect = CGRectMake(
        x = left.toDouble(),
        y = top.toDouble(),
        width = (right - left).toDouble(),
        height = (bottom - top).toDouble()
    )
    return CGPathCreateWithRect(rect, transform = null)!!
}
