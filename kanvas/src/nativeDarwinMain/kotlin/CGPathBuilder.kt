package com.juul.krayon.kanvas

import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreGraphics.CGMutablePathRef
import platform.CoreGraphics.CGPathAddCurveToPoint
import platform.CoreGraphics.CGPathAddLineToPoint
import platform.CoreGraphics.CGPathAddQuadCurveToPoint
import platform.CoreGraphics.CGPathAddRelativeArc
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
        // FIXME: Pretty sure this is wrong
        super.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
        // Use height as radius, we can transform by the aspect ratio to achieve the stretch.
        val height = (bottom - top).absoluteValue
        val aspect = (right - left).absoluteValue / height
        CGPathAddRelativeArc(
            buffer,
            matrix = CGAffineTransformMakeScale(sx = aspect.toDouble(), sy = 1.0),
            // TODO: Check if I need to scale x by the inverse transform
            x = (right - left) / 2.0,
            y = (bottom - top) / 2.0,
            radius = height.toDouble(),
            startAngle = startAngle * PI / 180,
            delta = sweepAngle * PI / 180,
        )
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
internal inline fun Path.withCGPath(
    crossinline action: (cgPath: CGPathRef) -> Unit,
) {
    val buffer = CGPathCreateMutable()!!
    try {
        val cgPath = buildWith(CGPathBuilder(buffer))
        try {
            action(cgPath)
        } finally {
            CGPathRelease(cgPath)
        }
    } finally {
        CGPathRelease(buffer)
    }
}
