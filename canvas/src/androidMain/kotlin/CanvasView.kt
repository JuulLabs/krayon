package com.juul.krayon.canvas

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

/** Base class for custom [View]s implemented using Krayon. */
abstract class CanvasView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var isFirstDraw = true

    @SuppressLint("DrawAllocation")
    final override fun onDraw(canvas: android.graphics.Canvas) {
        AndroidCanvas(canvas).withDpScale(resources.displayMetrics) {
            if (isFirstDraw) {
                isFirstDraw = false
                beforeFirstDrawWithKrayon(this)
            }
            drawWithKrayon(this)
        }
    }

    /**
     * Called before the first call to [drawWithKrayon]. Generally, calls to [Canvas.buildPaint] and [Canvas.buildPath]
     * should be called in this function and cached to variables to avoid allocations on successive draw calls.
     */
    protected abstract fun beforeFirstDrawWithKrayon(canvas: Canvas<AndroidPaint, AndroidPath>)

    /**
     * Implement this to do your drawing. The canvas has been pre-scaled such that 1 unit equals 1dp.
     *
     * Unlike [View.onDraw], you should use [Canvas.width] and [Canvas.height] instead of the view height and width.
     *
     * Use [beforeFirstDrawWithKrayon] to do allocations (like calls to [Canvas.buildPaint] and [Canvas.buildPath]) when possible.
     */
    protected abstract fun drawWithKrayon(canvas: Canvas<AndroidPaint, AndroidPath>)
}
