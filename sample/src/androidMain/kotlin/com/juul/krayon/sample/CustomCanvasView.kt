package com.juul.krayon.sample

import android.content.Context
import android.util.AttributeSet
import com.juul.krayon.color.Color
import com.juul.krayon.color.black
import com.juul.krayon.color.white
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.KanvasView
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Paint.Stroke.Cap.Round
import com.juul.krayon.kanvas.Paint.Stroke.Dash.Pattern
import com.juul.krayon.kanvas.Paint.Text.Alignment.Left
import android.graphics.Path as AndroidPath

class CustomCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : KanvasView(context, attrs) {

    private val linePaint = Paint.Stroke(black, width = 2f, cap = Round, dash = Pattern(0f, 4f))
    private val textPaint = Paint.Text(black, size = 18f, alignment = Left, Fonts.robotoSlab)
    private var circlePaint = Paint.FillAndStroke(
        Paint.Fill(white),
        Paint.Stroke(black, width = 4f)
    )

    var circleColor: Color
        get() = circlePaint.fill.color
        set(value) {
            circlePaint = circlePaint.copy(fill = Paint.Fill(value))
            invalidate()
        }

    override fun onDraw(canvas: Kanvas<AndroidPath>) = with(canvas) {
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
