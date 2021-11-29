package com.juul.krayon.kanvas

import kotlin.test.Test
import kotlin.test.assertEquals

private const val EPSILON = 0.125f

class RelativePathBuilderTests {

    @Test
    fun arcTo_fromRightToBottom_hasCorrectEndPosition() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.arcTo(0f, 0f, 100f, 100f, 0f, 90f, false)
        val state = pathBuilder.getState()
        assertEquals(50f, actual = state.lastX, absoluteTolerance = EPSILON)
        assertEquals(100f, actual = state.lastY, absoluteTolerance = EPSILON)
    }

    @Test
    fun arcTo_withForceMove_changesContourStart() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.arcTo(0f, 0f, 100f, 100f, 0f, 90f, true)
        val state = pathBuilder.getState()
        assertEquals(100f, actual = state.closeToX, absoluteTolerance = EPSILON)
        assertEquals(50f, actual = state.closeToY, absoluteTolerance = EPSILON)
    }

    @Test
    fun arcTo_withoutForceMove_doesNotChangeContourStart() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.arcTo(0f, 0f, 100f, 100f, 0f, 90f, false)
        val state = pathBuilder.getState()
        assertEquals(0f, state.closeToX)
        assertEquals(0f, state.closeToY)
    }

    @Test
    fun relativeCubicTo_withNonZeroLastLocation_hasCorrectEndPosition() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.relativeCubicTo(10f, 0f, 20f, 30f, 30f, 30f)
        val state = pathBuilder.getState()
        assertEquals(80f, state.lastX)
        assertEquals(80f, state.lastY)
    }

    @Test
    fun relativeCubicTo_withNonZeroLastLocation_passesThroughToCubicTo() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.relativeCubicTo(10f, 0f, 20f, 30f, 30f, 30f)
        pathBuilder.verifySingle("called cubicTo with correct arguments") { call ->
            call.callableName == "cubicTo" && call.arguments == listOf(60f, 50f, 70f, 80f, 80f, 80f)
        }
    }

    @Test
    fun relativeLineTo_withNonZeroLastLocation_hasCorrectEndPosition() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.relativeLineTo(30f, 30f)
        val state = pathBuilder.getState()
        assertEquals(80f, state.lastX)
        assertEquals(80f, state.lastY)
    }

    @Test
    fun relativeLineTo_withNonZeroLastLocation_passesThroughToLineTo() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.relativeLineTo(30f, 30f)
        pathBuilder.verifySingle("called lineTo with correct arguments") { call ->
            call.callableName == "lineTo" && call.arguments == listOf(80f, 80f)
        }
    }

    @Test
    fun moveTo_startsNewContour() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        val state = pathBuilder.getState()
        assertEquals(50f, state.closeToX)
        assertEquals(50f, state.closeToY)
    }

    @Test
    fun relativeMoveTo_withNonZeroLastLocation_hasCorrectEndPosition() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.relativeMoveTo(30f, 30f)
        val state = pathBuilder.getState()
        assertEquals(80f, state.lastX)
        assertEquals(80f, state.lastY)
    }

    @Test
    fun relativeMoveTo_withNonZeroLastLocation_passesThroughToMoveTo() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.relativeMoveTo(30f, 30f)
        pathBuilder.verifySingle("called moveTo with correct arguments") { call ->
            call.callableName == "moveTo" && call.arguments == listOf(80f, 80f)
        }
    }

    @Test
    fun relativeQuadraticTo_withNonZeroLastLocation_hasCorrectEndPosition() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.relativeQuadraticTo(10f, 0f, 30f, 30f)
        val state = pathBuilder.getState()
        assertEquals(80f, state.lastX)
        assertEquals(80f, state.lastY)
    }

    @Test
    fun relativeQuadraticTo_withNonZeroLastLocation_passesThroughToQuadraticTo() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.relativeQuadraticTo(10f, 0f, 30f, 30f)
        pathBuilder.verifySingle("called quadraticTo with correct arguments") { call ->
            call.callableName == "quadraticTo" && call.arguments == listOf(60f, 50f, 80f, 80f)
        }
    }

    @Test
    fun close_withNonZeroContourStart_hasCorrectEndPosition() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.lineTo(100f, 100f)
        pathBuilder.close()
        val state = pathBuilder.getState()
        assertEquals(50f, state.lastX)
        assertEquals(50f, state.lastY)
    }

    @Test
    fun reset_zeroesContourStart() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.reset()
        val state = pathBuilder.getState()
        assertEquals(0f, state.closeToX)
        assertEquals(0f, state.closeToY)
    }

    @Test
    fun reset_zeroesEndPosition() {
        val pathBuilder = CallRecordingPathBuilder()
        pathBuilder.moveTo(50f, 50f)
        pathBuilder.reset()
        val state = pathBuilder.getState()
        assertEquals(0f, state.lastX)
        assertEquals(0f, state.lastY)
    }
}
