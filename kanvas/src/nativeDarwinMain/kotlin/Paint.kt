package com.juul.krayon.kanvas

import kotlinx.cinterop.refTo
import platform.CoreGraphics.CGColorCreate
import platform.CoreGraphics.CGColorRelease
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceGetNumberOfComponents
import platform.CoreGraphics.CGColorSpaceRef
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextClip
import platform.CoreGraphics.CGContextCopyPath
import platform.CoreGraphics.CGContextDrawLinearGradient
import platform.CoreGraphics.CGContextDrawRadialGradient
import platform.CoreGraphics.CGContextFillPath
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextSetLineCap
import platform.CoreGraphics.CGContextSetLineDash
import platform.CoreGraphics.CGContextSetLineJoin
import platform.CoreGraphics.CGContextSetLineWidth
import platform.CoreGraphics.CGContextSetRGBFillColor
import platform.CoreGraphics.CGContextSetRGBStrokeColor
import platform.CoreGraphics.CGContextStrokePath
import platform.CoreGraphics.CGGradientCreateWithColorComponents
import platform.CoreGraphics.CGGradientRef
import platform.CoreGraphics.CGGradientRelease
import platform.CoreGraphics.CGLineCap
import platform.CoreGraphics.CGLineJoin
import platform.CoreGraphics.CGPathGetBoundingBox
import platform.CoreGraphics.CGPathRef
import platform.CoreGraphics.CGPathRelease
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.kCGGradientDrawsAfterEndLocation
import platform.CoreGraphics.kCGGradientDrawsBeforeStartLocation
import platform.QuartzCore.CAGradientLayer
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
    // FIXME: Path copy could be dodged on linear/radial
    val path = CGContextCopyPath(context)!!
    try {
        CGContextSaveGState(context)
        CGContextClip(context)
        when (paint) {
            is Paint.Gradient.Linear -> drawLinearGradient(context, paint)
            is Paint.Gradient.Radial -> drawRadialGradient(context, paint)
            is Paint.Gradient.Sweep -> drawConicGradient(context, paint, path)
        }
        CGContextRestoreGState(context)
    } finally {
        CGPathRelease(path)
    }
}

private fun drawConicGradient(context: CGContextRef, paint: Paint.Gradient.Sweep, path: CGPathRef) {
    val components = paint.stops.map { stop ->
        doubleArrayOf(
            stop.color.red / 255.0,
            stop.color.green / 255.0,
            stop.color.blue / 255.0,
            stop.color.alpha / 255.0,
        )
    }
    val colors = withCGColorSpaceDeviceRGB { colorspace ->
        components.map { CGColorCreate(colorspace, it.refTo(0)) }
    }
    try {
        val layer = CAGradientLayer()
        layer.type = kCAGradientLayerConic
        layer.colors = colors
        layer.locations = paint.stops.map { it.offset.toDouble() }
        layer.setFrame(CGPathGetBoundingBox(path))
        layer.renderInContext(context)
    } finally {
        colors.forEach(::CGColorRelease)
    }
}

private fun drawLinearGradient(context: CGContextRef, paint: Paint.Gradient.Linear) {
    withCGGradientRef(paint.stops) { gradient ->
        CGContextDrawLinearGradient(
            context,
            gradient,
            startPoint = CGPointMake(paint.startX.toDouble(), paint.startY.toDouble()),
            endPoint = CGPointMake(paint.endX.toDouble(), paint.endY.toDouble()),
            options = kCGGradientDrawsBeforeStartLocation and kCGGradientDrawsAfterEndLocation,
        )
    }
}

private fun drawRadialGradient(context: CGContextRef, paint: Paint.Gradient.Radial) {
    withCGGradientRef(paint.stops) { gradient ->
        val center = CGPointMake(paint.centerX.toDouble(), paint.centerY.toDouble())
        CGContextDrawRadialGradient(
            context,
            gradient,
            startCenter = center,
            startRadius = 0.0,
            endCenter = center,
            endRadius = paint.radius.toDouble(),
            options = kCGGradientDrawsAfterEndLocation,
        )
    }
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
    val dashLengths = when (val dash = paint.dash) {
        Paint.Stroke.Dash.None -> null
        is Paint.Stroke.Dash.Pattern -> dash.intervals.map { it.toDouble() }.toDoubleArray()
        else -> error("Unreachable.")
    }
    CGContextSetLineDash(
        context,
        phase = 0.0,
        lengths = dashLengths?.refTo(0),
        count = (dashLengths?.size ?: 0).toULong(),
    )
    CGContextStrokePath(context)
}

/** Handles memory management for creating and using a [CGGradientRef]. */
private inline fun withCGGradientRef(
    stops: List<Paint.Gradient.Stop>,
    crossinline actions: (CGGradientRef) -> Unit,
) {
    withCGColorSpaceDeviceRGB { colorspace ->
        val components = stops.flatMap { stop ->
            listOf(
                stop.color.red / 255.0,
                stop.color.green / 255.0,
                stop.color.blue / 255.0,
                stop.color.alpha / 255.0,
            )
        }.toDoubleArray()
        val locations = stops.map { it.offset.toDouble() }.toDoubleArray()
        val gradient = CGGradientCreateWithColorComponents(
            colorspace,
            components = components.refTo(0),
            locations = locations.refTo(0),
            count = locations.size.toULong(),
        )!!
        try {
            actions(gradient)
        } finally {
            CGGradientRelease(gradient)
        }
    }
}

private inline fun <T> withCGColorSpaceDeviceRGB(
    crossinline actions: (CGColorSpaceRef) -> T,
): T {
    val colorspace = CGColorSpaceCreateDeviceRGB()!!
    check(CGColorSpaceGetNumberOfComponents(colorspace).toInt() == 4) {
        "Unexpected number of components. Expected `4` but was `${CGColorSpaceGetNumberOfComponents(colorspace)}`."
    }
    try {
        return actions(colorspace)
    } finally {
        CGColorSpaceRelease(colorspace)
    }
}
