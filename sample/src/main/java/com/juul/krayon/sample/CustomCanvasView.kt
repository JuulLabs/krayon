package com.juul.krayon.sample

import android.content.Context
import android.util.AttributeSet
import com.juul.krayon.kanvas.Color
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.KanvasView
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.toAndroid
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

class CustomCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : KanvasView(context, attrs) {

    private val linePaint = Paint.Stroke(Color.black, 1f, Paint.Stroke.Cap.Round).toAndroid(context)
    private val textPaint = Paint.Text(Color.black, 18f, Paint.Text.Alignment.Left, Fonts.robotoSlab).toAndroid(context)
    private var circlePaint = Paint.Stroke(Color.black, 4f).toAndroid(context)

    var circleColor: Color
        get() = Color(circlePaint.color)
        set(value) {
            circlePaint.color = value.argb
            invalidate()
        }

    override fun onDraw(canvas: Kanvas<AndroidPaint, AndroidPath>) = with(canvas) {
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
