package com.juul.krayon.kanvas

import com.juul.krayon.color.black
import kotlin.test.Test

class KanvasExtensionTests {

    @Test
    fun checkWithClipMakesCorrectCalls() {
        val canvas = CallRecordingKanvas(100f, 100f)
        canvas.withClip(Clip.Rect(0f, 0f, 5f, 5f)) {
            val paint = canvas.buildPaint(Paint.Stroke(black, 1f))
            canvas.drawLine(0f, 0f, 10f, 10f, paint)
        }
        canvas.verifyFirst("pushClip called first") { it.callableName == "pushClip" }
        canvas.verifyAny("lambda was called") { it.callableName == "buildPaint" }
        canvas.verifyAny("lambda was called") { it.callableName == "drawLine" }
        canvas.verifyLast("pop was called last") { it.callableName == "pop" }
        canvas.verifyCallCount(4)
    }

    @Test
    fun checkWithTransformMakesCorrectCalls() {
        val canvas = CallRecordingKanvas(100f, 100f)
        canvas.withTransform(Transform.Scale(horizontal = 2f)) {
            val paint = canvas.buildPaint(Paint.Stroke(black, 1f))
            canvas.drawLine(0f, 0f, 10f, 10f, paint)
        }
        canvas.verifyFirst("pushTransform called first") { it.callableName == "pushTransform" }
        canvas.verifyAny("lambda was called") { it.callableName == "buildPaint" }
        canvas.verifyAny("lambda was called") { it.callableName == "drawLine" }
        canvas.verifyLast("pop was called last") { it.callableName == "pop" }
        canvas.verifyCallCount(4)
    }
}
