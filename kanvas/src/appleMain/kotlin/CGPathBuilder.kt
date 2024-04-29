package com.juul.krayon.kanvas

import platform.CoreGraphics.CGAffineTransformConcat
import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreGraphics.CGAffineTransformMakeTranslation
import platform.CoreGraphics.CGMutablePathRef
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
import platform.CoreGraphics.CGPathRetain
import kotlin.math.PI
import kotlin.math.absoluteValue

/**
 * We're in the realm of manual memory management with no real guards. Be careful! Both the input
 * [buffer] and the paths returned from [build] must be manually released via [CGPathRelease].
 *
 * See [Path.withCGPath] as a way to make using this slightly less insane.
 */
internal class CGPathBuilder(
    private val buffer: CGMutablePathRef,
) : RelativePathBuilder<CGPathRef>() {

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(x, y)
        CGPathMoveToPoint(buffer, null, x.toDouble(), y.toDouble())
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(x, y)
        CGPathAddLineToPoint(buffer, null, x.toDouble(), y.toDouble())
    }

    override fun arcTo(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, forceMoveTo: Boolean) {
        val height = (bottom - top).absoluteValue.toDouble()
        val width = (right - left).absoluteValue.toDouble()
        if (height == 0.0 && width == 0.0) {
            when (forceMoveTo) {
                true -> moveTo(left, top)
                false -> lineTo(left, top)
            }
        } else {
            super.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
            val aspect = width / height
            val cx = (right + left) / 2.0
            val cy = (bottom + top) / 2.0
            val radius = height / 2.0
            val startRads = startAngle * PI / 180
            val endRads = (startAngle + sweepAngle) * PI / 180
            val clockwise = sweepAngle < 0
            CGPathAddArc(
                buffer,
                m = CGAffineTransformConcat(
                    CGAffineTransformMakeScale(aspect, 1.0),
                    CGAffineTransformMakeTranslation(cx, cy),
                ),
                x = 0.0,
                y = 0.0,
                radius = radius,
                startAngle = startRads,
                endAngle = endRads,
                clockwise = clockwise,
            )
        }
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        super.quadraticTo(controlX, controlY, endX, endY)
        CGPathAddQuadCurveToPoint(
            buffer,
            null,
            cpx = controlX.toDouble(),
            cpy = controlY.toDouble(),
            x = endX.toDouble(),
            y = endY.toDouble(),
        )
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        super.cubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
        CGPathAddCurveToPoint(
            buffer,
            null,
            cp1x = beginControlX.toDouble(),
            cp1y = beginControlY.toDouble(),
            cp2x = endControlX.toDouble(),
            cp2y = endControlY.toDouble(),
            x = endX.toDouble(),
            y = endY.toDouble(),
        )
    }

    override fun close() {
        super.close()
        CGPathCloseSubpath(buffer)
    }

    override fun reset() {
        // TODO: Investigate making Path native builds not call reset. Fine as-is because this is internal.
        // throw UnsupportedOperationException("Cannot reset a CGMutablePath. Create a new builder instead.")
    }

    override fun build(): CGPathRef = CGPathCreateCopy(buffer)!!
}

/**
 * Performs [action] with a [CGPathRef], automatically releasing it when the block finishes.
 *
 * If you wish to hold a reference to the path outside the block, it is your responsibility to call
 * [CGPathRetain] before the block finishes, and [CGPathRelease] when you are done with the path.
 */
internal inline fun <T> Path.withCGPath(
    crossinline action: (cgPath: CGPathRef) -> T,
): T {
    val buffer = CGPathCreateMutable()!!
    try {
        val cgPath = buildWith(CGPathBuilder(buffer))
        try {
            return action(cgPath)
        } finally {
            CGPathRelease(cgPath)
        }
    } finally {
        CGPathRelease(buffer)
    }
}
