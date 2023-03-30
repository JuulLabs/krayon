package com.juul.krayon.kanvas

import com.juul.krayon.color.Color
import com.juul.krayon.color.lerp
import kotlinx.cinterop.refTo
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceRef
import platform.CoreGraphics.CGColorSpaceRelease
import platform.CoreGraphics.CGContextAddLineToPoint
import platform.CoreGraphics.CGContextAddPath
import platform.CoreGraphics.CGContextBeginPath
import platform.CoreGraphics.CGContextClip
import platform.CoreGraphics.CGContextCopyPath
import platform.CoreGraphics.CGContextDrawLinearGradient
import platform.CoreGraphics.CGContextDrawRadialGradient
import platform.CoreGraphics.CGContextFillPath
import platform.CoreGraphics.CGContextGetInterpolationQuality
import platform.CoreGraphics.CGContextMoveToPoint
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextSetLineCap
import platform.CoreGraphics.CGContextSetLineDash
import platform.CoreGraphics.CGContextSetLineJoin
import platform.CoreGraphics.CGContextSetLineWidth
import platform.CoreGraphics.CGContextSetRGBFillColor
import platform.CoreGraphics.CGContextSetRGBStrokeColor
import platform.CoreGraphics.CGContextSetShouldAntialias
import platform.CoreGraphics.CGContextStrokePath
import platform.CoreGraphics.CGGradientCreateWithColorComponents
import platform.CoreGraphics.CGGradientRef
import platform.CoreGraphics.CGGradientRelease
import platform.CoreGraphics.CGLineCap
import platform.CoreGraphics.CGLineJoin
import platform.CoreGraphics.CGPathRelease
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.kCGGradientDrawsAfterEndLocation
import platform.CoreGraphics.kCGGradientDrawsBeforeStartLocation
import platform.CoreGraphics.kCGInterpolationLow
import platform.CoreGraphics.kCGInterpolationMedium
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

internal fun Paint.drawCurrentPath(context: CGContextRef) {
    when (this) {
        is Paint.FillAndStroke -> {
            // Drawing the current path clears it, so make a copy first for drawing a second time.
            val copy = CGContextCopyPath(context)
            try {
                drawCurrentPathFill(context, fill)
                CGContextAddPath(context, copy)
            } finally {
                CGPathRelease(copy)
            }
            drawCurrentPathStroke(context, stroke)
        }

        is Paint.GradientAndStroke -> {
            // Drawing the current path clears it, so make a copy first for drawing a second time.
            val copy = CGContextCopyPath(context)
            try {
                drawCurrentPathGradient(context, gradient)
                CGContextAddPath(context, copy)
            } finally {
                CGPathRelease(copy)
            }
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
    CGContextSaveGState(context)
    CGContextClip(context)
    when (paint) {
        is Paint.Gradient.Linear -> drawLinearGradient(context, paint)
        is Paint.Gradient.Radial -> drawRadialGradient(context, paint)
        is Paint.Gradient.Sweep -> drawConicGradient(context, paint)
    }
    CGContextRestoreGState(context)
}

private fun drawConicGradient(context: CGContextRef, paint: Paint.Gradient.Sweep) {
    // FIXME: It might be possible to use CAGradientLayer, but I couldn't get that working.
    //  TODO: See if iOS works -- the docs only put the warning for OS X specifically.
    // Approximate by splitting into a bunch of triangles around the center point.
    val sweeps = paint.stops.asSequence().zipWithNext()
    for ((start, end) in sweeps) {
        val distance = end.offset - start.offset
        val approxTotalSegmentCount = when (CGContextGetInterpolationQuality(context)) {
            kCGInterpolationLow -> 64
            kCGInterpolationMedium -> 128
            else -> 256 // Enough segments to fit an 8-bit color channel across the sweep without banding
        }
        val segmentCount = ceil(distance * approxTotalSegmentCount).toInt().coerceAtLeast(3)
        val segmentWidth = distance / segmentCount
        CGContextSetShouldAntialias(context, false)
        for (segment in 0 until segmentCount) {
            val color = lerp(start.color, end.color, segment.toFloat() / (segmentCount - 1f))
            val segmentOffsetStart = start.offset + segmentWidth * segment
            val segmentOffsetEnd = segmentOffsetStart + segmentWidth
            CGContextBeginPath(context)
            val angleStart = segmentOffsetStart * 2 * PI
            val angleEnd = segmentOffsetEnd * 2 * PI
            CGContextMoveToPoint(context, paint.centerX.toDouble(), paint.centerY.toDouble())
            val radius = 100_000 // Any large number works. Goal is to shoot off the edges of the context.
            CGContextAddLineToPoint(context, paint.centerX + cos(angleStart) * radius, paint.centerY + sin(angleStart) * radius)
            CGContextAddLineToPoint(context, paint.centerX + cos(angleEnd) * radius, paint.centerY + sin(angleEnd) * radius)
            drawCurrentPathFill(context, Paint.Fill(color))
        }
        CGContextSetShouldAntialias(context, true)
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
            stop.color.getComponents().asIterable()
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
    try {
        return actions(colorspace)
    } finally {
        CGColorSpaceRelease(colorspace)
    }
}

private fun Color.getComponents(): DoubleArray =
    doubleArrayOf(red / 255.0, green / 255.0, blue / 255.0, alpha / 255.0)
