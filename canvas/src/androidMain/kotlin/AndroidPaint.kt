package com.juul.krayon.canvas

import android.graphics.Paint as AndroidPaint

/** Converts a Krayon [Paint] into an [AndroidPaint] used by [AndroidCanvas]. */
public fun Paint.toAndroid(): AndroidPaint = when (this) {
    is Paint.Fill -> androidPaint(this)
    is Paint.Stroke -> androidPaint(this)
    is Paint.Text -> androidPaint(this)
}

private fun androidPaint(source: Paint.Fill) = AndroidPaint().apply {
    style = AndroidPaint.Style.STROKE
    isAntiAlias = true
    color = source.color.argb
}

private fun androidPaint(source: Paint.Stroke) = AndroidPaint().apply {
    style = AndroidPaint.Style.STROKE
    isAntiAlias = true
    color = source.color.argb
    strokeWidth = source.width
    strokeCap = when (source.cap) {
        Paint.Stroke.Cap.Butt -> AndroidPaint.Cap.BUTT
        Paint.Stroke.Cap.Round -> AndroidPaint.Cap.ROUND
        Paint.Stroke.Cap.Square -> AndroidPaint.Cap.SQUARE
    }
    strokeJoin = when (source.join) {
        is Paint.Stroke.Join.Miter -> AndroidPaint.Join.MITER
        Paint.Stroke.Join.Round -> AndroidPaint.Join.ROUND
        Paint.Stroke.Join.Bevel -> AndroidPaint.Join.BEVEL
    }
    if (source.join is Paint.Stroke.Join.Miter) {
        strokeMiter = source.join.limit
    }
}

private fun androidPaint(source: Paint.Text) = AndroidPaint().apply {
    style = AndroidPaint.Style.FILL
    isAntiAlias = true
    color = source.color.argb
    textSize = source.size
    textAlign = when (source.alignment) {
        Paint.Text.Alignment.Left -> AndroidPaint.Align.LEFT
        Paint.Text.Alignment.Center -> AndroidPaint.Align.CENTER
        Paint.Text.Alignment.Right -> AndroidPaint.Align.RIGHT
    }
}
