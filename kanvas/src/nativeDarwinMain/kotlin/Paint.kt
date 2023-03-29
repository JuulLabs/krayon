package com.juul.krayon.kanvas

import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextSetRGBFillColor
import platform.CoreGraphics.CGContextSetRGBStrokeColor
import platform.CoreGraphics.CGPathDrawingMode
import platform.CoreGraphics.CGTextDrawingMode

internal val Paint.pathDrawingMode: CGPathDrawingMode
    get() = when (this) {
        is Paint.FillAndStroke, is Paint.GradientAndStroke -> CGPathDrawingMode.kCGPathFillStroke
        is Paint.Fill, is Paint.Gradient -> CGPathDrawingMode.kCGPathFill
        is Paint.Stroke -> CGPathDrawingMode.kCGPathStroke
        else -> error("`pathDrawingMode` must not be called on `Paint.Text`, but was `$this`.")
    }

internal val Paint.textDrawingMode: CGTextDrawingMode
    get() = when (this) {
        is Paint.Text -> CGTextDrawingMode.kCGTextFill
        else -> error("`textDrawingMode` must only be called on `Paint.Text`, but was `$this`.")
    }

internal fun Paint.applyTo(context: CGContextRef) {
    when (this) {
        is Paint.FillAndStroke -> {
            applyFill(context, fill)
            applyStroke(context, stroke)
        }

        is Paint.GradientAndStroke -> {
            applyGradient(context, gradient)
            applyStroke(context, stroke)
        }

        is Paint.Fill -> applyFill(context, this)
        is Paint.Gradient -> applyGradient(context, this)
        is Paint.Stroke -> applyStroke(context, this)
        is Paint.Text -> applyText(context, this)
    }
}

private fun applyFill(context: CGContextRef, paint: Paint.Fill) {
    CGContextSetRGBFillColor(
        context,
        paint.color.red / 255.0,
        paint.color.green / 255.0,
        paint.color.blue / 255.0,
        paint.color.alpha / 255.0,
    )
}

private fun applyGradient(context: CGContextRef, paint: Paint.Gradient) {
}

private fun applyStroke(context: CGContextRef, paint: Paint.Stroke) {
    try {
        CGContextSetRGBStrokeColor(
            context,
            paint.color.red / 255.0,
            paint.color.green / 255.0,
            paint.color.blue / 255.0,
            paint.color.alpha / 255.0,
        )
    } catch (e: Exception) {
        throw RuntimeException("Set stroke failed", e)
    }
}

private fun applyText(context: CGContextRef, paint: Paint.Text) {
}
