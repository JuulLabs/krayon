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

internal sealed class Segment {
    data class MoveTo(
        val x: Float,
        val y: Float,
    ) : Segment()

    data class RelativeMoveTo(
        val x: Float,
        val y: Float,
    ) : Segment()

    data class LineTo(
        val x: Float,
        val y: Float,
    ) : Segment()

    data class RelativeLineTo(
        val x: Float,
        val y: Float,
    ) : Segment()

    data class ArcTo(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
        val startAngle: Float,
        val sweepAngle: Float,
        val forceMoveTo: Boolean,
    ) : Segment()

    data class QuadraticTo(
        val controlX: Float,
        val controlY: Float,
        val endX: Float,
        val endY: Float,
    ) : Segment()

    data class RelativeQuadraticTo(
        val controlX: Float,
        val controlY: Float,
        val endX: Float,
        val endY: Float,
    ) : Segment()

    data class CubicTo(
        val beginControlX: Float,
        val beginControlY: Float,
        val endControlX: Float,
        val endControlY: Float,
        val endX: Float,
        val endY: Float,
    ) : Segment()

    data class RelativeCubicTo(
        val beginControlX: Float,
        val beginControlY: Float,
        val endControlX: Float,
        val endControlY: Float,
        val endX: Float,
        val endY: Float,
    ) : Segment()

    object Close : Segment()
}

internal class SegmentedPathBuilder : PathBuilder<SegmentedPath> {

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

    override fun arcTo(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        forceMoveTo: Boolean,
    ) {
        segments += ArcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        segments += QuadraticTo(controlX, controlY, endX, endY)
    }

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        segments += RelativeQuadraticTo(controlX, controlY, endX, endY)
    }

    override fun cubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ) {
        segments += CubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun relativeCubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ) {
        segments += RelativeCubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun close() {
        segments += Close
    }

    override fun reset() {
        segments.clear()
    }

    // `toList` used to copy segments, preventing future mutation
    override fun build(): SegmentedPath = SegmentedPath(segments.toList())
}

internal data class SegmentedPath(
    val segments: List<Segment>,
) {
    fun <P> rebuildWith(builder: PathBuilder<P>): P {
        builder.reset()
        for (segment in segments) {
            with(segment) {
                when (this) {
                    is MoveTo -> builder.moveTo(x, y)
                    is RelativeMoveTo -> builder.relativeMoveTo(x, y)
                    is LineTo -> builder.lineTo(x, y)
                    is RelativeLineTo -> builder.relativeLineTo(x, y)
                    is ArcTo -> builder.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
                    is QuadraticTo -> builder.quadraticTo(controlX, controlY, endX, endY)
                    is RelativeQuadraticTo -> builder.relativeQuadraticTo(controlX, controlY, endX, endY)
                    is CubicTo -> builder.cubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
                    is RelativeCubicTo -> builder.relativeCubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
                    is Close -> builder.close()
                }
            }
        }
        return builder.build()
    }
}
