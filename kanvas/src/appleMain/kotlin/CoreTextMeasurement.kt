package com.juul.krayon.kanvas

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFRelease
import platform.CoreGraphics.CGFloatVar
import platform.CoreText.CTLineCreateWithAttributedString
import platform.CoreText.CTLineGetTypographicBounds
import platform.CoreText.kCTFontAttributeName

/**
 * Standalone [MeasureText] implementation backed by Core Text. Useful for measuring text during
 * layout, when a [CGContextKanvas] is not available.
 */
public object CoreTextMeasurement : MeasureText {

    override fun measureText(text: CharSequence, paint: Paint.Text): TextMetrics {
        val line = withCFAttributedString(text.toString()) { attributedString ->
            attributedString[kCTFontAttributeName] = paint.ctFont
            CTLineCreateWithAttributedString(attributedString)
        }
        return memScoped {
            val ascent = alloc<CGFloatVar>()
            val descent = alloc<CGFloatVar>()
            val leading = alloc<CGFloatVar>()
            val width = CTLineGetTypographicBounds(line, ascent.ptr, descent.ptr, leading.ptr)
            CFRelease(line)
            TextMetrics(
                width = width.toFloat(),
                ascent = ascent.value.toFloat(),
                descent = descent.value.toFloat(),
            )
        }
    }
}
