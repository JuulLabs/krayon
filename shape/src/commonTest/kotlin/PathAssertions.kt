package com.juul.krayon.shape

import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.PathBuilder
import kotlin.test.assertEquals

internal data class PathOp(val name: String, val args: List<Float>)

internal class RecordingPathBuilder : PathBuilder<List<PathOp>> {
    private val ops = mutableListOf<PathOp>()

    override fun moveTo(x: Float, y: Float) {
        ops += PathOp("M", listOf(x, y))
    }

    override fun relativeMoveTo(x: Float, y: Float) {
        ops += PathOp("m", listOf(x, y))
    }

    override fun lineTo(x: Float, y: Float) {
        ops += PathOp("L", listOf(x, y))
    }

    override fun relativeLineTo(x: Float, y: Float) {
        ops += PathOp("l", listOf(x, y))
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
        ops += PathOp("A", listOf(left, top, right, bottom, startAngle, sweepAngle, if (forceMoveTo) 1f else 0f))
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        ops += PathOp("Q", listOf(controlX, controlY, endX, endY))
    }

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        ops += PathOp("q", listOf(controlX, controlY, endX, endY))
    }

    override fun cubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ) {
        ops += PathOp("C", listOf(beginControlX, beginControlY, endControlX, endControlY, endX, endY))
    }

    override fun relativeCubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ) {
        ops += PathOp("c", listOf(beginControlX, beginControlY, endControlX, endControlY, endX, endY))
    }

    override fun close() {
        ops += PathOp("Z", emptyList())
    }

    override fun reset() {
        ops.clear()
    }

    override fun build(): List<PathOp> = ops.toList()
}

internal fun Path.ops(): List<PathOp> = buildWith(RecordingPathBuilder())

/**
 * Compares two paths command-by-command with floating-point tolerance. Unlike [Path.equals], this
 * ignores tiny numeric differences (e.g. `Float` rounding versus the full-precision D3 fixtures).
 */
internal fun assertPathEquals(expected: Path, actual: Path, absoluteTolerance: Float = 1e-3f) {
    val e = expected.ops()
    val a = actual.ops()
    assertEquals(e.size, a.size, "Different number of path commands.\nexpected: $e\nactual:   $a")
    for (i in e.indices) {
        assertEquals(e[i].name, a[i].name, "Different command at index $i.\nexpected: $e\nactual:   $a")
        assertEquals(e[i].args.size, a[i].args.size, "Different argument count at index $i.\nexpected: $e\nactual:   $a")
        for (j in e[i].args.indices) {
            assertEquals(
                expected = e[i].args[j],
                actual = a[i].args[j],
                absoluteTolerance = absoluteTolerance,
                message = "Different argument $j of command $i.\nexpected: $e\nactual:   $a",
            )
        }
    }
}
