package com.juul.krayon.sample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.juul.krayon.canvas.Canvas
import com.juul.krayon.canvas.CanvasView
import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.Paint
import kotlin.random.Random
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

class CanvasActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DemoView(this))
    }

    class DemoView(context: Context) : CanvasView(context) {
        private lateinit var linePaint: AndroidPaint
        private lateinit var circlePaint: AndroidPaint

        init {
            setOnClickListener { invalidateKrayonState() }
        }

        override fun onKrayonSetup(canvas: Canvas<AndroidPaint, AndroidPath>) {
            linePaint = canvas.buildPaint(Paint.Stroke(Color.black, 1f, Paint.Stroke.Cap.Round))
        }

        override fun onKrayonStateChange(canvas: Canvas<AndroidPaint, AndroidPath>) {
            val color = Color(0xFF000000.toInt() or Random.nextInt(0xFFFFFF))
            circlePaint = canvas.buildPaint(Paint.Stroke(color, 4f, Paint.Stroke.Cap.Round))
        }

        override fun onKrayonDraw(canvas: Canvas<AndroidPaint, AndroidPath>) = with(canvas) {
            drawLine(16f, 16f, width - 16f, height - 16f, linePaint)
            val vScale = (height - 32f) / (width - 32f)
            var hOffset = 0f
            var vOffset = 0f
            while (hOffset < width - 16 || vOffset < height - 16) {
                drawCircle(16f + hOffset, 16f + vOffset, 12f, circlePaint)
                hOffset += 24
                vOffset += 24 * vScale
            }
        }
    }
}
