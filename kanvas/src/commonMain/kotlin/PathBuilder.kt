package com.juul.krayon.kanvas

/** Builds paths. */
public interface PathBuilder<out P> {

    // TODO: There's a lot more operations that can be done here, but this seems like a nice minimal set.

    /** Set the beginning of the next contour to the point [x], [y]. */
    public fun moveTo(x: Float, y: Float)

    /** Same as [moveTo], except coordinates are relative to the current location. */
    public fun relativeMoveTo(x: Float, y: Float)

    /** Add a line from the last point to the specified point [x], [y]. */
    public fun lineTo(x: Float, y: Float)

    /** Same as [lineTo], except coordinates are relative to the current location. */
    public fun relativeLineTo(x: Float, y: Float)

    /** Append the specified arc to the path as a new contour. */
    public fun arcTo(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        forceMoveTo: Boolean,
    )

    /**
     * Add a quadratic bezier from the last point, approaching control point
     * [controlX], [controlY], and ending at point [endX], [endY].
     */
    public fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float)

    /** Same as [quadraticTo], except coordinates are relative to the current location. */
    public fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float)

    /**
     * Add a cubic bezier from the last point, approaching control points
     * [beginControlX], [beginControlY] and [endControlX], [endControlY],
     * and ending at point [endX], [endY].
     */
    public fun cubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    )

    /** Same as [cubicTo], except coordinates are relative to the current location. */
    public fun relativeCubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    )

    /** Close the current contour. */
    public fun close()

    /** Start over! */
    public fun reset()

    /**
     * Returns the path built by previous function calls. If [P] is mutable, then future builder
     * calls should not modify it and multiple calls to [build] must not return the same instance.
     */
    public fun build(): P
}
