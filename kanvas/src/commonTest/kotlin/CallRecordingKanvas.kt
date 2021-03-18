package com.juul.krayon.kanvas

import com.juul.krayon.color.Color

object UnitPaint
object UnitPath

class CallRecordingKanvas(
    width: Float,
    height: Float
) : Kanvas<UnitPaint, UnitPath>, CallRecord {

    private val recorder = CallRecorder()
    override val functionCalls: List<FunctionCall>
        get() = recorder.functionCalls

    private val _width = width
    override val width: Float
        get() {
            recorder.record(this::width.getter)
            return _width
        }

    private val _height = height
    override val height: Float
        get() {
            recorder.record(this::height.getter)
            return _height
        }

    override fun buildPaint(paint: Paint): UnitPaint {
        recorder.record(this::buildPaint, paint)
        return UnitPaint
    }

    override fun buildPath(actions: PathBuilder<*>.() -> Unit): UnitPath {
        recorder.record(this::buildPath, actions)
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
        recorder.record(this::drawArc, left, top, right, bottom, startAngle, sweepAngle, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: UnitPaint) {
        recorder.record(this::drawCircle, centerX, centerY, radius, paint)
    }

    override fun drawColor(color: Color) {
        recorder.record(this::drawColor, color)
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: UnitPaint) {
        recorder.record(this::drawLine, startX, startY, endX, endY, paint)
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: UnitPaint) {
        recorder.record(this::drawOval, left, top, right, bottom, paint)
    }

    override fun drawPath(path: UnitPath, paint: UnitPaint) {
        recorder.record(this::drawPath, path, paint)
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: UnitPaint) {
        recorder.record(this::drawRect, left, top, right, bottom, paint)
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: UnitPaint) {
        recorder.record(this::drawText, text, x, y, paint)
    }

    override fun pushClip(clip: Clip<UnitPath>) {
        recorder.record(this::pushClip, clip)
    }

    override fun pushTransform(transform: Transform) {
        recorder.record(this::pushTransform, transform)
    }

    override fun pop() {
        recorder.record(this::pop)
    }
}
