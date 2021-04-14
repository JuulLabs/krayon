package com.juul.krayon.kanvas

class CallRecordingPathBuilder : RelativePathBuilder<UnitPath>(), CallRecord {
    private val recorder = CallRecorder()
    override val calls: List<Call>
        get() = recorder.calls

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(x, y)
        recorder.record(this::moveTo, x, y)
    }

    override fun relativeMoveTo(x: Float, y: Float) {
        super.relativeMoveTo(x, y)
        recorder.record(this::relativeMoveTo, x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(x, y)
        recorder.record(this::lineTo, x, y)
    }

    override fun relativeLineTo(x: Float, y: Float) {
        super.relativeLineTo(x, y)
        recorder.record(this::relativeLineTo, x, y)
    }

    override fun arcTo(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, forceMoveTo: Boolean) {
        super.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
        recorder.record(this::arcTo, left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        super.quadraticTo(controlX, controlY, endX, endY)
        recorder.record(this::quadraticTo, controlX, controlY, endX, endY)
    }

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        super.relativeQuadraticTo(controlX, controlY, endX, endY)
        recorder.record(this::relativeQuadraticTo, controlX, controlY, endX, endY)
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        super.cubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
        recorder.record(this::cubicTo, beginControlX, beginControlY, endControlX, endControlY, endX, endY)
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
        recorder.record(this::relativeCubicTo, beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun close() {
        super.close()
        recorder.record(this::close)
    }

    override fun reset() {
        super.reset()
        recorder.record(this::reset)
    }

    override fun build(): UnitPath {
        recorder.record(this::build)
        return UnitPath
    }
}
