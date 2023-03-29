package com.juul.krayon.kanvas

import kotlinx.cinterop.refTo
import platform.CoreGraphics.CGContextFillPath
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextSetLineCap
import platform.CoreGraphics.CGContextSetLineDash
import platform.CoreGraphics.CGContextSetLineJoin
import platform.CoreGraphics.CGContextSetLineWidth
import platform.CoreGraphics.CGContextSetRGBFillColor
import platform.CoreGraphics.CGContextSetRGBStrokeColor
import platform.CoreGraphics.CGContextStrokePath
import platform.CoreGraphics.CGLineCap
import platform.CoreGraphics.CGLineJoin
import platform.CoreGraphics.CGPathDrawingMode
import platform.CoreGraphics.CGTextDrawingMode
import platform.QuartzCore.CAGradientLayer
import platform.QuartzCore.CAGradientLayerType
import platform.QuartzCore.kCAGradientLayerConic

internal fun Paint.drawCurrentPath(context: CGContextRef) {
    when (this) {
        is Paint.FillAndStroke -> {
            drawCurrentPathFill(context, fill)
            drawCurrentPathStroke(context, stroke)
        }

        is Paint.GradientAndStroke -> {
            drawCurrentPathGradient(context, gradient)
            drawCurrentPathStroke(context, stroke)
        }

        is Paint.Fill -> drawCurrentPathFill(context, this)
        is Paint.Gradient -> drawCurrentPathGradient(context, this)
        is Paint.Stroke -> drawCurrentPathStroke(context, this)
        is Paint.Text -> error("`drawCurrentPath` must not be called on `Paint.Text`, but was `$this`.")
    }
}

private fun drawCurrentPathFill(context: CGContextRef, paint: Paint.Fill) {
    CGContextSetRGBFillColor(
        context,
        paint.color.red / 255.0,
        paint.color.green / 255.0,
        paint.color.blue / 255.0,
        paint.color.alpha / 255.0,
    )
    CGContextFillPath(context)
}

private fun drawCurrentPathGradient(context: CGContextRef, paint: Paint.Gradient) {
    // TODO: Actually implement gradients. Unfortunately, Core Graphics doesn't have conic gradients,
    //       so those will have a different code path. Dodging all that complexity with a placeholder
    drawCurrentPathFill(context, Paint.Fill(paint.stops.first().color))
}

private fun drawCurrentPathStroke(context: CGContextRef, paint: Paint.Stroke) {
    CGContextSetRGBStrokeColor(
        context,
        paint.color.red / 255.0,
        paint.color.green / 255.0,
        paint.color.blue / 255.0,
        paint.color.alpha / 255.0,
    )
    CGContextSetLineWidth(context, paint.width.toDouble())
    CGContextSetLineCap(
        context,
        cap = when (paint.cap) {
            Paint.Stroke.Cap.Butt -> CGLineCap.kCGLineCapButt
            Paint.Stroke.Cap.Round -> CGLineCap.kCGLineCapRound
            Paint.Stroke.Cap.Square -> CGLineCap.kCGLineCapSquare
        },
    )
    CGContextSetLineJoin(
        context,
        join = when (paint.join) {
            Paint.Stroke.Join.Round -> CGLineJoin.kCGLineJoinRound
            Paint.Stroke.Join.Bevel -> CGLineJoin.kCGLineJoinMiter
            is Paint.Stroke.Join.Miter -> CGLineJoin.kCGLineJoinMiter
            else -> error("Unreachable.")
        },
    )
    val dashPattern = when (paint.dash) {
        Paint.Stroke.Dash.None -> null
        is Paint.Stroke.Dash.Pattern -> paint.dash.intervals.map { it.toDouble() }.toDoubleArray()
        else -> error("Unreachable.")
    }
    CGContextSetLineDash(
        context,
        phase = 0.0,
        lengths = dashPattern?.refTo(0),
        count = (dashPattern?.size ?: 0).toULong()
    )
    CGContextStrokePath(context)
}
