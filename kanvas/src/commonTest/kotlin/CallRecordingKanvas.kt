package com.juul.krayon.kanvas

import com.juul.krayon.color.Color

object UnitPaint
object UnitPath

class CallRecordingKanvas(
    width: Float,
    height: Float
) : Kanvas<UnitPaint, UnitPath>, CallRecord {

    private val recorder = CallRecorder()
    override val calls: List<Call>
        get() = recorder.calls

    private val _width = width
    override val width: Float
        get() {
            recorder.record("width")
            return _width
        }

    private val _height = height
    override val height: Float
        get() {
            recorder.record("height")
            return _height
        }

    override fun buildPaint(paint: Paint): UnitPaint {
        recorder.record("buildPaint", paint)
        return UnitPaint
    }

    override fun buildPath(actions: Path): UnitPath {
        recorder.record("buildPath", actions)
        return UnitPath
    }

    override fun drawArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        paint: UnitPaint,
    ) {
        recorder.record("drawArc", left, top, right, bottom, startAngle, sweepAngle, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: UnitPaint) {
        recorder.record("drawCircle", centerX, centerY, radius, paint)
    }

    override fun drawColor(color: Color) {
        recorder.record("drawColor", color)
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: UnitPaint) {
        recorder.record("drawLine", startX, startY, endX, endY, paint)
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: UnitPaint) {
        recorder.record("drawOval", left, top, right, bottom, paint)
    }

    override fun drawPath(path: UnitPath, paint: UnitPaint) {
        recorder.record("drawPath", path, paint)
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: UnitPaint) {
        recorder.record("drawRect", left, top, right, bottom, paint)
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: UnitPaint) {
        recorder.record("drawText", text, x, y, paint)
    }

    override fun pushClip(clip: Clip<UnitPath>) {
        recorder.record("pushClip", clip)
    }

    override fun pushTransform(transform: Transform) {
        recorder.record("pushTransform", transform)
    }

    override fun pop() {
        recorder.record("pop")
    }
}
