package com.juul.krayon.kanvas

class CallRecordingPathBuilder : RelativePathBuilder<Unit>(), CallRecord {
    private val recorder = CallRecorder()
    override val calls: List<Call>
        get() = recorder.calls

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(x, y)
        recorder.record("moveTo", x, y)
    }

    override fun relativeMoveTo(x: Float, y: Float) {
        super.relativeMoveTo(x, y)
        recorder.record("relativeMoveTo", x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(x, y)
        recorder.record("lineTo", x, y)
    }

    override fun relativeLineTo(x: Float, y: Float) {
        super.relativeLineTo(x, y)
        recorder.record("relativeLineTo", x, y)
    }

    override fun arcTo(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, forceMoveTo: Boolean) {
        super.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
        recorder.record("arcTo", left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        super.quadraticTo(controlX, controlY, endX, endY)
        recorder.record("quadraticTo", controlX, controlY, endX, endY)
    }

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        super.relativeQuadraticTo(controlX, controlY, endX, endY)
        recorder.record("relativeQuadraticTo", controlX, controlY, endX, endY)
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        super.cubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
        recorder.record("cubicTo", beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun relativeCubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ) {
        super.relativeCubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
        recorder.record("relativeCubicTo", beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun close() {
        super.close()
        recorder.record("close")
    }

    override fun reset() {
        super.reset()
        recorder.record("reset")
    }

    override fun build() {
        recorder.record("build")
    }
}
