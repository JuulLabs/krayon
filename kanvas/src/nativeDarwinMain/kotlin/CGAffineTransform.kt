package com.juul.krayon.kanvas

import kotlinx.cinterop.CValue
import platform.CoreGraphics.CGAffineTransform
import platform.CoreGraphics.CGAffineTransformConcat
import platform.CoreGraphics.CGAffineTransformMake
import platform.CoreGraphics.CGAffineTransformMakeTranslation
import platform.CoreGraphics.CGAffineTransformRotate
import platform.CoreGraphics.CGAffineTransformScale
import platform.CoreGraphics.CGAffineTransformTranslate
import platform.CoreGraphics.CGContextConcatCTM
import platform.CoreGraphics.CGContextRef
import kotlin.math.PI

/**
 * Like [CGAffineTransformMakeTranslation] and similar, but for skew.
 *
 * TODO: Test this. Assuming correct for now since the params line up with HTML's implementation.
 */
internal fun CGAffineTransformMakeSkew(
    horizontal: Double,
    vertical: Double,
): CValue<CGAffineTransform> = CGAffineTransformMake(
    a = 1.0,
    b = vertical,
    c = horizontal,
    d = 1.0,
    tx = 0.0,
    ty = 0.0,
)

/**
 * Like [CGAffineTransformTranslate] and similar, but for skew.
 */
internal fun CGAffineTransformSkew(
    matrix: CValue<CGAffineTransform>,
    horizontal: Double,
    vertical: Double,
): CValue<CGAffineTransform> = CGAffineTransformConcat(
    matrix,
    CGAffineTransformMakeSkew(horizontal, vertical),
)

internal fun CGContextSkewCTM(
    context: CGContextRef,
    horizontal: Double,
    vertical: Double,
) {
    CGContextConcatCTM(
        context,
        CGAffineTransformMakeSkew(horizontal, vertical),
    )
}

internal fun Transform.asCGAffineTransform(): CValue<CGAffineTransform> {
    // While theoretically complete, this implementation is almost wholly untested.
    var buffer = CGAffineTransformMakeTranslation(0.0, 0.0)
    fun Transform.applyToBuffer() {
        when (this) {
            is Transform.InOrder -> {
                transformations.forEach { it.applyToBuffer() }
            }

            is Transform.Scale -> if (pivotX == 0f && pivotY == 0f) {
                buffer = CGAffineTransformScale(buffer, horizontal.toDouble(), vertical.toDouble())
            } else {
                split().applyToBuffer()
            }

            is Transform.Rotate -> if (pivotX == 0f && pivotY == 0f) {
                buffer = CGAffineTransformRotate(buffer, degrees * PI / 180)
            } else {
                split().applyToBuffer()
            }

            is Transform.Translate -> {
                buffer = CGAffineTransformTranslate(buffer, horizontal.toDouble(), vertical.toDouble())
            }

            is Transform.Skew -> {
                buffer = CGAffineTransformSkew(buffer, horizontal.toDouble(), vertical.toDouble())
            }
        }
    }
    applyToBuffer()
    return buffer
}
