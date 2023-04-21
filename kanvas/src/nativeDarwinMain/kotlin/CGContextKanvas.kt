package com.juul.krayon.kanvas

import com.juul.krayon.color.Color
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.value
import platform.CoreGraphics.CGContextAddPath
import platform.CoreGraphics.CGContextBeginPath
import platform.CoreGraphics.CGContextClip
import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextRotateCTM
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM
import kotlin.math.PI

/**
 * It is the calling code's responsibility to ensure that [unmanagedContext] outlives this object.
 *
 * @constructor When calling from Swift, pass `Unmanaged.passUnretained/passRetained(yourCgContext)`
 */
public class CGContextKanvas(
    private val unmanagedContext: CGContextRef,
    width: Double,
    height: Double,
) : Kanvas {

    /** When calling from Swift, use `&yourCgContext`. */
    public constructor(
        ptr: CPointerVarOf<CGContextRef>,
        width: Double,
        height: Double,
    ) : this(unmanagedContext = ptr.value!!, width, height)

    override val width: Float = width.toFloat()

    override val height: Float = height.toFloat()

    override fun drawArc(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, paint: Paint) {
        drawAsPath(paint) {
            arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo = false)
        }
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: Paint) {
        drawOval(centerX - radius, centerY - radius, centerX + radius, centerY + radius, paint)
    }

    override fun drawColor(color: Color) {
        drawRect(0f, 0f, width, height, Paint.Fill(color))
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint) {
        drawAsPath(paint) {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        drawAsPath(paint) {
            moveTo(right, (bottom - top) / 2)
            arcTo(left, top, right, bottom, 0f, 180f, false)
            arcTo(left, top, right, bottom, 180f, 180f, false)
            close()
        }
    }

    override fun drawPath(path: Path, paint: Paint) {
        require(paint !is Paint.Text) { "`drawPath` must not be called with `Paint.Text`, but was $paint." }

        withInverseY {
            CGContextBeginPath(unmanagedContext)
            path.withCGPath { cgPath ->
                CGContextAddPath(unmanagedContext, cgPath)
            }
            paint.drawCurrentPath(unmanagedContext)
        }
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        drawAsPath(paint) {
            moveTo(left, top)
            lineTo(right, top)
            lineTo(right, bottom)
            lineTo(left, bottom)
            close()
        }
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: Paint) {
        require(paint is Paint.Text)
        // Text, unlike other rendering, can't just be rendered in a y-inverted transform.
        // Manually adjust by doing `height - y` and calling it a day.
        drawText(unmanagedContext, text.toString(), x.toDouble(), (height - y).toDouble(), paint)
    }

    override fun pushClip(clip: Clip) {
        CGContextSaveGState(unmanagedContext)
        CGContextBeginPath(unmanagedContext)
        clip.path.withCGPath { cgPath ->
            CGContextAddPath(unmanagedContext, cgPath)
        }
        CGContextClip(unmanagedContext)
    }

    override fun pushTransform(transform: Transform) {
        CGContextSaveGState(unmanagedContext)
        fun Transform.applyToCurrentTransformationMatrix() {
            when (this) {
                is Transform.InOrder -> {
                    transformations.forEach { it.applyToCurrentTransformationMatrix() }
                }

                is Transform.Scale -> if (pivotX == 0f && pivotY == 0f) {
                    CGContextScaleCTM(unmanagedContext, horizontal.toDouble(), vertical.toDouble())
                } else {
                    split().applyToCurrentTransformationMatrix()
                }

                is Transform.Rotate -> if (pivotX == 0f && pivotY == 0f) {
                    CGContextRotateCTM(unmanagedContext, degrees * PI / 180)
                } else {
                    split().applyToCurrentTransformationMatrix()
                }

                is Transform.Translate -> {
                    CGContextTranslateCTM(unmanagedContext, horizontal.toDouble(), -vertical.toDouble())
                }

                is Transform.Skew -> {
                    CGContextSkewCTM(unmanagedContext, horizontal.toDouble(), vertical.toDouble())
                }
            }
        }
        transform.applyToCurrentTransformationMatrix()
    }

    override fun pop() {
        CGContextRestoreGState(unmanagedContext)
    }

    /**
     * It's not optimal to allocate a [Path] for every draw call, but it's the easiest API to work
     * with to avoid reimplementing things like the arc translation.
     *
     * In the future, we'll want to just handle each function individually.
     */
    private inline fun drawAsPath(
        paint: Paint,
        crossinline pathSpec: PathBuilder<*>.() -> Unit,
    ) {
        drawPath(Path { pathSpec() }, paint)
    }

    /**
     * Apple's coordinate system is the inverse of everywhere else. The generally foolproof way to
     * handle this is using the transformation matrix to flip the canvas vertically.
     *
     * Do _not_ use this with text -- doing so will result in upside down lettering.
     */
    private inline fun withInverseY(
        crossinline actions: () -> Unit,
    ) {
        CGContextSaveGState(unmanagedContext)
        CGContextTranslateCTM(unmanagedContext, 0.0, height.toDouble())
        CGContextScaleCTM(unmanagedContext, 1.0, -1.0)
        try {
            actions()
        } finally {
            CGContextRestoreGState(unmanagedContext)
        }
    }
}
