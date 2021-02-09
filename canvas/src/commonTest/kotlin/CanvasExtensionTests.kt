package com.juul.krayon.canvas

import kotlin.test.Test

class CanvasExtensionTests {

    @Test
    fun checkWithClipMakesCorrectCalls() {
        val canvas = CallRecordingCanvas()
        canvas.withClip(Clip.Rect(0f, 0f, 5f, 5f)) {
            drawLine(0f, 0f, 10f, 10f, Paint.Stroke(Color.black, 1f))
        }
        canvas.verifyFirst("pushClip called first") { it.function == canvas::pushClip }
        canvas.verifyAny("lambda was called") { it.function == canvas::drawLine }
        canvas.verifyLast("pop was called last") { it.function == canvas::pop }
        canvas.verifyCallCount(3)
    }

    @Test
    fun checkWithTransformMakesCorrectCalls() {
        val canvas = CallRecordingCanvas()
        canvas.withTransform(Transform.Scale(horizontal = 2f)) {
            canvas.drawLine(0f, 0f, 10f, 10f, Paint.Stroke(Color.black, 1f))
        }
        canvas.verifyFirst("pushTransform called first") { it.function == canvas::pushTransform }
        canvas.verifyAny("lambda was called") { it.function == canvas::drawLine }
        canvas.verifyLast("pop was called last") { it.function == canvas::pop }
        canvas.verifyCallCount(3)
    }
}
