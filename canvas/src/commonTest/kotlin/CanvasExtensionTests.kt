package com.juul.krayon.canvas

import kotlin.test.Test

class CanvasExtensionTests {

    @Test
    fun checkWithClipMakesCorrectCalls() {
        val canvas = CallRecordingCanvas()
        canvas.withClip(Clip.Rect(0f, 0f, 5f, 5f)) {
            val paint = canvas.buildPaint(Paint.Stroke(Color.black, 1f))
            canvas.drawLine(0f, 0f, 10f, 10f, paint)
        }
        canvas.verifyFirst("pushClip called first") { it.function == canvas::pushClip }
        canvas.verifyAll("lambda was called") { it.function in arrayOf(canvas::buildPaint, canvas::drawLine) }
        canvas.verifyLast("pop was called last") { it.function == canvas::pop }
        canvas.verifyCallCount(4)
    }

    @Test
    fun checkWithTransformMakesCorrectCalls() {
        val canvas = CallRecordingCanvas()
        canvas.withTransform(Transform.Scale(horizontal = 2f)) {
            val paint = canvas.buildPaint(Paint.Stroke(Color.black, 1f))
            canvas.drawLine(0f, 0f, 10f, 10f, paint)
        }
        canvas.verifyFirst("pushTransform called first") { it.function == canvas::pushTransform }
        canvas.verifyAll("lambda was called") { it.function in arrayOf(canvas::buildPaint, canvas::drawLine) }
        canvas.verifyLast("pop was called last") { it.function == canvas::pop }
        canvas.verifyCallCount(4)
    }
}
