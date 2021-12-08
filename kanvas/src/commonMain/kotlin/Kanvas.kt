package com.juul.krayon.kanvas

import com.juul.krayon.color.Color

/** Something to draw on. Implementations are not required to be safe across multiple threads. */
public interface Kanvas<PATH> {

    /** Gets the width of the canvas. */
    public val width: Float

    /** Gets the height of the canvas. */
    public val height: Float

    /** Create a [PATH] understood by this canvas. The returned path must NOT have a reference to the canvas. */
    public fun buildPath(actions: Path): PATH

    /**
     * Draw an arc that fits in the oval defined by the rectangle [left], [top], [right], and [bottom], from
     * [startAngle] (in degrees, with 0 at the right) to [startAngle] + [sweepAngle].
     */
    public fun drawArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        paint: Paint,
    )

    /** Draw a circle at [centerX], [centerY] with size defined by its [radius]. */
    public fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: Paint)

    /** Draw a [color] over the entire canvas. Implementations may or may not restrict this to the current clip. */
    public fun drawColor(color: Color)

    /** Draw a line from [startX], [startY] to [endX], [endY]. */
    public fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint)

    /** Draw an oval defined by the rectangle [left], [top], [right], and [bottom]. */
    public fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint)

    /** Draws a path returned by [buildPath]. */
    public fun drawPath(path: PATH, paint: Paint)

    /** Draws the rectangle [left], [top], [right], [bottom]. */
    public fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint)

    /** Draws a string of [text] at [x], [y]. Exact horizontal behavior is controlled by the [paint]'s [alignment][Paint.Text.Alignment]. */
    public fun drawText(text: CharSequence, x: Float, y: Float, paint: Paint)

    /** Pushes a [clip] to the canvas. Remove it with [pop]. */
    public fun pushClip(clip: Clip<PATH>)

    /** Pushes a [transform] to the canvas. Remove it with [pop]. */
    public fun pushTransform(transform: Transform)

    /** Removes the last [pushClip] or [pushTransform] operation. */
    public fun pop()
}

/** Invokes [actions] inside of a [Kanvas.pushClip]/[Kanvas.pop] pair. */
public inline fun <PATH> Kanvas<PATH>.withClip(
    clip: Clip<PATH>,
    actions: Kanvas<PATH>.() -> Unit,
) {
    pushClip(clip)
    try {
        actions.invoke(this)
    } finally {
        pop()
    }
}

/** Invokes [actions] inside of a [Kanvas.pushTransform]/[Kanvas.pop] pair. */
public inline fun <PATH> Kanvas<PATH>.withTransform(
    transform: Transform,
    actions: Kanvas<PATH>.() -> Unit,
) {
    pushTransform(transform)
    try {
        actions.invoke(this)
    } finally {
        pop()
    }
}