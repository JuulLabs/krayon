package com.juul.krayon.sample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.juul.krayon.canvas.Canvas
import com.juul.krayon.canvas.CanvasView
import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.Paint
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

class CanvasActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DemoView(this))
    }

    class DemoView(context: Context) : CanvasView(context) {
        private lateinit var paint: AndroidPaint

        override fun beforeFirstDrawWithKrayon(canvas: Canvas<AndroidPaint, AndroidPath>) {
            paint = canvas.buildPaint(Paint.Stroke(Color.black, 4f, Paint.Stroke.Cap.Round))
        }

        override fun drawWithKrayon(canvas: Canvas<AndroidPaint, AndroidPath>) = with(canvas) {
            drawLine(16f, 16f, width - 16f, height - 16f, paint)
        }
    }
}
