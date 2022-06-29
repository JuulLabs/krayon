package com.juul.krayon.kanvas

import platform.CoreGraphics.CGAffineTransformConcat
import platform.CoreGraphics.CGAffineTransformIdentity
import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreGraphics.CGAffineTransformMakeTranslation
import platform.CoreGraphics.CGPathAddArc
import platform.CoreGraphics.CGPathAddCurveToPoint
import platform.CoreGraphics.CGPathAddLineToPoint
import platform.CoreGraphics.CGPathAddQuadCurveToPoint
import platform.CoreGraphics.CGPathCloseSubpath
import platform.CoreGraphics.CGPathCreateCopy
import platform.CoreGraphics.CGPathCreateMutable
import platform.CoreGraphics.CGPathMoveToPoint
import platform.CoreGraphics.CGPathRef
import platform.CoreGraphics.CGPathRelease
import kotlin.math.PI

public inline fun CGPath(path: Path): CGPathRef = CGPathBuilder().build(path)
public fun Path.toCGPath(): CGPathRef = CGPathBuilder().build(this)

public class CGPathBuilder : RelativePathBuilder<CGPathRef>() {

    private var buffer = CGPathCreateMutable()!!

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(x, y)
        CGPathMoveToPoint(buffer, m = null, x.toDouble(), y.toDouble())
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(x, y)
        CGPathAddLineToPoint(buffer, m = null, x.toDouble(), y.toDouble())
    }

    override fun arcTo(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, forceMoveTo: Boolean) {
        super.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
        val radiusX = (right - left) / 2.0
        val radiusY = (bottom - top) / 2.0
        val x = left + radiusX
        val y = top + radiusY
        val startAngleRadians = startAngle * PI / 180f
        val endAngleRadians = (startAngle + sweepAngle) * PI / 180f
        val anticlockwise = sweepAngle < 0f

        // CoreGraphics arcs only represent circles, not ellipses. Luckily, we can transform by a matrix,
        // so first scale it to achieve the ellipse shape then translate it to achieve the location.
        val radius = minOf(radiusX, radiusY)
        val scaleX = radiusX / radius
        val scaleY = radiusY / radius
        val transform = CGAffineTransformMakeScale(scaleX, scaleY)
            .concat(CGAffineTransformMakeTranslation(x, y))

        CGPathAddArc(buffer, transform, 0.0, 0.0, radius, startAngleRadians, endAngleRadians, clockwise = !anticlockwise)
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        super.quadraticTo(controlX, controlY, endX, endY)
        CGPathAddQuadCurveToPoint(buffer, m = null, controlX.toDouble(), controlY.toDouble(), endX.toDouble(), endY.toDouble())
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        super.cubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
        CGPathAddCurveToPoint(
            buffer,
            m = null,
            beginControlX.toDouble(),
            beginControlY.toDouble(),
            endControlX.toDouble(),
            endControlY.toDouble(),
            endX.toDouble(),
            endY.toDouble()
        )
    }

    override fun close() {
        super.close()
        CGPathCloseSubpath(buffer)
    }

    override fun reset() {
        super.reset()
        CGPathRelease(buffer)
        buffer = CGPathCreateMutable()!!
    }

    override fun build(): CGPathRef = CGPathCreateCopy(buffer)!!
}
