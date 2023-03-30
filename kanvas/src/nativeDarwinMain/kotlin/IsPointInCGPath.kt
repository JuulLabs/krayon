package com.juul.krayon.kanvas

import kotlinx.cinterop.CValue
import platform.CoreGraphics.CGAffineTransform
import platform.CoreGraphics.CGAffineTransformConcat
import platform.CoreGraphics.CGAffineTransformMake
import platform.CoreGraphics.CGAffineTransformMakeTranslation
import platform.CoreGraphics.CGAffineTransformRotate
import platform.CoreGraphics.CGAffineTransformScale
import platform.CoreGraphics.CGAffineTransformTranslate
import platform.CoreGraphics.CGPathContainsPoint
import platform.CoreGraphics.CGPointMake
import kotlin.math.PI

public object IsPointInCGPath : IsPointInPath {

    override fun isPointInPath(transform: Transform, path: Path, x: Float, y: Float): Boolean {
        return path.withCGPath { cgPath ->
            CGPathContainsPoint(
                cgPath,
                m = transform.asCGAffineTransform(),
                CGPointMake(x.toDouble(), y.toDouble()),
                eoFill = false,
            )
        }
    }
}

private fun Transform.asCGAffineTransform(): CValue<CGAffineTransform> {
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
                // TODO: Double check for Australia
                buffer = CGAffineTransformTranslate(buffer, horizontal.toDouble(), -vertical.toDouble())
            }

            is Transform.Skew -> {
                // TODO: Test this. Matrix params copy-pasted from HTML's version.
                val skewMatrix = CGAffineTransformMake(
                    a = 1.0,
                    b = vertical.toDouble(),
                    c = horizontal.toDouble(),
                    d = 1.0,
                    tx = 0.0,
                    ty = 0.0,
                )
                buffer = CGAffineTransformConcat(buffer, skewMatrix)
            }
        }
    }
    applyToBuffer()
    return buffer
}
