package com.juul.krayon.kanvas

import com.juul.krayon.kanvas.Segment.ArcTo
import com.juul.krayon.kanvas.Segment.Close
import com.juul.krayon.kanvas.Segment.CubicTo
import com.juul.krayon.kanvas.Segment.LineTo
import com.juul.krayon.kanvas.Segment.MoveTo
import com.juul.krayon.kanvas.Segment.QuadraticTo
import com.juul.krayon.kanvas.Segment.RelativeCubicTo
import com.juul.krayon.kanvas.Segment.RelativeLineTo
import com.juul.krayon.kanvas.Segment.RelativeMoveTo
import com.juul.krayon.kanvas.Segment.RelativeQuadraticTo

/**
 * Calls [Path.apply] and records the results for replay against future path builders. This is especially useful when a [Path]
 * performs potentially expensive calculations and its platform specific type is not cached.
 */
public fun Path.reify(): Path = ReifiedPathPathBuilder().build(this)

internal sealed class Segment {
    data class MoveTo(val x: Float, val y: Float) : Segment()
    data class RelativeMoveTo(val x: Float, val y: Float) : Segment()
    data class LineTo(val x: Float, val y: Float) : Segment()
    data class RelativeLineTo(val x: Float, val y: Float) : Segment()
    data class ArcTo(val left: Float, val top: Float, val right: Float, val bottom: Float, val startAngle: Float, val sweepAngle: Float, val forceMoveTo: Boolean) : Segment()
    data class QuadraticTo(val controlX: Float, val controlY: Float, val endX: Float, val endY: Float) : Segment()
    data class RelativeQuadraticTo(val controlX: Float, val controlY: Float, val endX: Float, val endY: Float) : Segment()
    data class CubicTo(val beginControlX: Float, val beginControlY: Float, val endControlX: Float, val endControlY: Float, val endX: Float, val endY: Float) : Segment()
    data class RelativeCubicTo(val beginControlX: Float, val beginControlY: Float, val endControlX: Float, val endControlY: Float, val endX: Float, val endY: Float) : Segment()
    object Close : Segment()
}

internal class ReifiedPathPathBuilder : PathBuilder<ReifiedPath> {

    private val segments = mutableListOf<Segment>()

    override fun moveTo(x: Float, y: Float) {
        segments += MoveTo(x, y)
    }

    override fun relativeMoveTo(x: Float, y: Float) {
        segments += RelativeMoveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        segments += LineTo(x, y)
    }

    override fun relativeLineTo(x: Float, y: Float) {
        segments += RelativeLineTo(x, y)
    }

    override fun arcTo(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, forceMoveTo: Boolean) {
        segments += ArcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        segments += QuadraticTo(controlX, controlY, endX, endY)
    }

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        segments += RelativeQuadraticTo(controlX, controlY, endX, endY)
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        segments += CubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun relativeCubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        segments += RelativeCubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun close() {
        segments += Close
    }

    override fun reset() {
        segments.clear()
    }

    // ArrayList used to copy segments, preventing future mutation
    override fun build(): ReifiedPath = ReifiedPath(ArrayList(segments))
}

internal data class ReifiedPath(
    internal val segments: List<Segment>
) : Path {
    override fun PathBuilder<*>.apply() {
        for (segment in segments) with(segment) {
            when (this) {
                is MoveTo -> moveTo(x, y)
                is RelativeMoveTo -> relativeMoveTo(x, y)
                is LineTo -> lineTo(x, y)
                is RelativeLineTo -> relativeLineTo(x, y)
                is ArcTo -> arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
                is QuadraticTo -> quadraticTo(controlX, controlY, endX, endY)
                is RelativeQuadraticTo -> RelativeQuadraticTo(controlX, controlY, endX, endY)
                is CubicTo -> cubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
                is RelativeCubicTo -> relativeCubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
                is Close -> close()
            }
        }
    }
}
