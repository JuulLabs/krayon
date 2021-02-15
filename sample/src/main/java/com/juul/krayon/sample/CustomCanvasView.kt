package com.juul.krayon.sample

import android.content.Context
import android.util.AttributeSet
import com.juul.krayon.canvas.Canvas
import com.juul.krayon.canvas.CanvasView
import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.Font
import com.juul.krayon.canvas.Paint
import com.juul.krayon.canvas.toAndroid
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

class CustomCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : CanvasView(context, attrs) {

    private val linePaint = Paint.Stroke(Color.black, 1f, Paint.Stroke.Cap.Round).toAndroid(context)
    private val textPaint = Paint.Text(Color.black, 18f, Paint.Text.Alignment.Left, Font("roboto_slab")).toAndroid(context)
    private var circlePaint = Paint.Stroke(Color.black, 4f).toAndroid(context)

    var circleColor: Color
        get() = Color(circlePaint.color)
        set(value) {
            circlePaint.color = value.argb
            invalidate()
        }

    override fun onDraw(canvas: Canvas<AndroidPaint, AndroidPath>) = with(canvas) {
        drawLine(16f, 16f, width - 16f, height - 16f, linePaint)
        val vScale = (height - 32f) / (width - 32f)
        var hOffset = 0f
        var vOffset = 0f
        while (hOffset < width - 16 || vOffset < height - 16) {
            drawCircle(16f + hOffset, 16f + vOffset, 12f, circlePaint)
            hOffset += 24
            vOffset += 24 * vScale
        }
        canvas.drawText("Show off text rendering", 16f, height - 32f, textPaint)
    }
}
