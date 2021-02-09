package com.juul.krayon.canvas

import android.graphics.Paint.Align
import android.graphics.Paint.Cap
import android.graphics.Paint.Join
import android.graphics.Paint.Style

/** Converts a Krayon [Paint] into an Android [Paint][android.graphics.Paint]. */
internal fun Paint.toAndroid() = when (this) {
    is Paint.Fill -> androidPaint(this)
    is Paint.Stroke -> androidPaint(this)
    is Paint.Text -> androidPaint(this)
}

private fun androidPaint(source: Paint.Fill) = android.graphics.Paint().apply {
    style = Style.STROKE
    isAntiAlias = true
    color = source.color.argb
}

private fun androidPaint(source: Paint.Stroke) = android.graphics.Paint().apply {
    style = Style.STROKE
    isAntiAlias = true
    color = source.color.argb
    strokeWidth = source.width
    strokeCap = when (source.cap) {
        Paint.Stroke.Cap.Butt -> Cap.BUTT
        Paint.Stroke.Cap.Round -> Cap.ROUND
        Paint.Stroke.Cap.Square -> Cap.SQUARE
    }
    strokeJoin = when (source.join) {
        is Paint.Stroke.Join.Miter -> Join.MITER
        Paint.Stroke.Join.Round -> Join.ROUND
        Paint.Stroke.Join.Bevel -> Join.BEVEL
    }
    if (source.join is Paint.Stroke.Join.Miter) {
        strokeMiter = source.join.limit
    }
}

private fun androidPaint(source: Paint.Text) = android.graphics.Paint().apply {
    style = Style.FILL
    isAntiAlias = true
    color = source.color.argb
    textSize = source.size
    textAlign = when (source.alignment) {
        Paint.Text.Alignment.Left -> Align.LEFT
        Paint.Text.Alignment.Center -> Align.CENTER
        Paint.Text.Alignment.Right -> Align.RIGHT
    }
}
