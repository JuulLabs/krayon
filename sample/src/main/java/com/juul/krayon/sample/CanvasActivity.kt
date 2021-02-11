package com.juul.krayon.sample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.juul.krayon.canvas.Canvas
import com.juul.krayon.canvas.CanvasView
import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.Paint
import com.juul.krayon.canvas.nextColor
import com.juul.krayon.canvas.toAndroid
import kotlin.random.Random
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

class CanvasActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DemoView(this))
    }

    class DemoView(context: Context) : CanvasView(context) {
        private val linePaint = Paint.Stroke(Color.black, 1f, Paint.Stroke.Cap.Round).toAndroid()
        private var circlePaint = Paint.Stroke(Random.nextColor(), 4f).toAndroid()

        init {
            setOnClickListener {
                circlePaint = Paint.Stroke(Random.nextColor(), 4f).toAndroid()
                invalidate()
            }
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
        }
    }
}
