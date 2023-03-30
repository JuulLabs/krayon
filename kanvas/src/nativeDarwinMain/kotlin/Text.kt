@file:OptIn(ExperimentalUnsignedTypes::class)

package com.juul.krayon.kanvas

import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextSetRGBFillColor
import platform.CoreGraphics.CGContextSetTextPosition
import platform.CoreText.CTFontRef
import platform.CoreText.CTLineCreateWithAttributedString
import platform.CoreText.CTLineDraw
import platform.CoreText.CTLineGetTypographicBounds
import platform.CoreText.kCTFontAttributeName
import platform.CoreText.kCTForegroundColorFromContextAttributeName

internal fun drawText(context: CGContextRef, text: String, x: Double, y: Double, paint: Paint.Text) {
    val line = withCFAttributedString(text) { attributedString ->
        attributedString[kCTFontAttributeName] = paint.ctFont
        attributedString[kCTForegroundColorFromContextAttributeName] = kCFBooleanTrue
        CTLineCreateWithAttributedString(attributedString)
    }
    val offset = CTLineGetTypographicBounds(line, null, null, null) * when (paint.alignment) {
        Paint.Text.Alignment.Left -> 0.0
        Paint.Text.Alignment.Center -> 0.5
        Paint.Text.Alignment.Right -> 1.0
    }
    CGContextSetRGBFillColor(
        context,
        paint.color.red / 255.0,
        paint.color.green / 255.0,
        paint.color.blue / 255.0,
        paint.color.alpha / 255.0,
    )
    CGContextSetTextPosition(context, x - offset, y)
    CTLineDraw(line, context)
    CFRelease(line)
}

private val Paint.Text.ctFont: CTFontRef
    get() = font.names.asSequence()
        .map { name -> FontCache[name, size] }
        .filterNotNull()
        .first()
