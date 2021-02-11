package com.juul.krayon.canvas

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.View
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath

/** Base class for custom [View]s implemented using Krayon. */
abstract class CanvasView(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val krayonCanvas = AndroidCanvas(null, scalingFactor = applyDimension(COMPLEX_UNIT_DIP, 1f, resources.displayMetrics))

    @SuppressLint("WrongCall") // false positive because our custom draw function shares the name `onDraw`
    final override fun onDraw(canvas: android.graphics.Canvas) {
        krayonCanvas.setCanvas(canvas)
        onDraw(krayonCanvas)
    }

    /**
     * Implement this to do your drawing. The canvas has been pre-scaled such that 1 unit equals 1dp.
     *
     * Like [View.onDraw], you should avoid allocation in this function.
     *
     * Unlike [View.onDraw], you should use [Canvas.width] and [Canvas.height] instead of the view's
     * width and height.
     */
    protected abstract fun onDraw(canvas: Canvas<AndroidPaint, AndroidPath>)
}
