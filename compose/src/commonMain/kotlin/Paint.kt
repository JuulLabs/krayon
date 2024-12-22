package com.juul.krayon.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke.Companion.DefaultMiter
import com.juul.krayon.kanvas.Paint
import androidx.compose.ui.graphics.Color as ComposeColor

internal val Paint.strokeOrNull: Paint.Stroke?
    get() = when (this) {
        is Paint.Stroke -> this
        is Paint.FillAndStroke -> this.stroke
        is Paint.GradientAndStroke -> this.stroke
        else -> null
    }

internal val Paint.Stroke.composeCap: StrokeCap
    get() = when (this.cap) {
        Paint.Stroke.Cap.Butt -> StrokeCap.Butt
        Paint.Stroke.Cap.Round -> StrokeCap.Round
        Paint.Stroke.Cap.Square -> StrokeCap.Square
    }
internal val Paint.Stroke.composeJoin: StrokeJoin
    get() = when (this.join) {
        Paint.Stroke.Join.Round -> StrokeJoin.Round
        Paint.Stroke.Join.Bevel -> StrokeJoin.Bevel
        is Paint.Stroke.Join.Miter -> StrokeJoin.Miter
    }

internal val Paint.Stroke.composePathEffect: PathEffect?
    get() = when (val dash = this.dash) {
        is Paint.Stroke.Dash.Pattern -> PathEffect.dashPathEffect(dash.intervals.toFloatArray())
        else -> null
    }

internal val Paint.drawStyle: DrawStyle get() = when (this) {
    is Paint.Fill -> this.drawStyle
    is Paint.Stroke -> this.drawStyle
    is Paint.Gradient -> this.drawStyle
    is Paint.Text -> TODO("Not yet implemented")
    is Paint.FillAndStroke -> throw UnsupportedOperationException("FillAndStroke cannot be converted to a singular draw style.")
    is Paint.GradientAndStroke -> throw UnsupportedOperationException("GradientAndStroke cannot be converted to a singular draw style.")
}
internal val Paint.Fill.drawStyle: DrawStyle get() = Fill
internal val Paint.Stroke.drawStyle: DrawStyle
    get() = Stroke(
        width = width,
        miter = (join as? Paint.Stroke.Join.Miter)?.limit ?: DefaultMiter,
        cap = composeCap,
        join = composeJoin,
        pathEffect = composePathEffect,
    )
internal val Paint.Gradient.drawStyle: DrawStyle get() = Fill

internal fun Paint.floodBrush(): Brush? = when (this) {
    is Paint.Fill -> this.toBrush()
    is Paint.FillAndStroke -> this.fill.toBrush()
    is Paint.Gradient -> this.toBrush()
    is Paint.GradientAndStroke -> this.gradient.toBrush()
    else -> null
}

internal fun Paint.strokeBrush(): Brush? = when (this) {
    is Paint.Stroke -> this.toBrush()
    is Paint.FillAndStroke -> this.stroke.toBrush()
    is Paint.GradientAndStroke -> this.stroke.toBrush()
    else -> null
}

internal fun Paint.toBrush(): Brush = when (this) {
    is Paint.Fill -> this.toBrush()
    is Paint.Stroke -> this.toBrush()
    is Paint.Gradient -> this.toBrush()
    is Paint.Text -> TODO("Not yet implemented")
    is Paint.FillAndStroke -> throw UnsupportedOperationException("FillAndStroke cannot be converted to a singular brush.")
    is Paint.GradientAndStroke -> throw UnsupportedOperationException("GradientAndStroke cannot be converted to a singular brush.")
}

internal fun Paint.Fill.toBrush(): Brush = SolidColor(color.toCompose())

internal fun Paint.Stroke.toBrush(): Brush = SolidColor(color.toCompose())

internal fun Paint.Gradient.toBrush(): Brush = when (this) {
    is Paint.Gradient.Linear -> this.toBrush()
    is Paint.Gradient.Radial -> this.toBrush()
    is Paint.Gradient.Sweep -> this.toBrush()
}

private val Paint.Gradient.composeStops: Array<Pair<Float, ComposeColor>>
    get() = Array(stops.size) { index -> stops[index].let { (offset, color) -> offset to color.toCompose() } }

private fun Paint.Gradient.Linear.toBrush(): Brush = Brush.linearGradient(
    *composeStops,
    start = Offset(startX, startY),
    end = Offset(endX, endY),
)

private fun Paint.Gradient.Radial.toBrush(): Brush = Brush.radialGradient(*composeStops, center = Offset(centerX, centerY), radius = radius)

private fun Paint.Gradient.Sweep.toBrush(): Brush = Brush.sweepGradient(*composeStops, center = Offset(centerX, centerY))
