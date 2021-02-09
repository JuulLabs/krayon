package com.juul.krayon.canvas

class CallRecordingCanvas : Canvas<Path>, CallRecord {

    private val recorder = CallRecorder()
    override val functionCalls: List<FunctionCall>
        get() = recorder.functionCalls

    override fun drawArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        useCenter: Boolean,
        paint: Paint
    ) {
        recorder.record(this::drawArc, left, top, right, bottom, startAngle, sweepAngle, useCenter, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: Paint) {
        recorder.record(this::drawCircle, centerX, centerY, radius, paint)
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint.Stroke) {
        recorder.record(this::drawLine, startX, startY, endX, endY, paint)
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        recorder.record(this::drawOval, left, top, right, bottom, paint)
    }

    override fun buildPath(actions: Path.Builder<*>.() -> Unit): Path = TODO()

    override fun drawPath(path: Path, paint: Paint) {
        recorder.record(this::drawPath, path, paint)
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: Paint.Text) {
        recorder.record(this::drawText, text, x, y, paint)
    }

    override fun pushClip(clip: Clip) {
        recorder.record(this::pushClip, clip)
    }

    override fun pushTransform(transform: Transform) {
        recorder.record(this::pushTransform, transform)
    }

    override fun pop() {
        recorder.record(this::pop)
    }
}
