package com.juul.krayon.kanvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.View

/** Base class for custom [View]s implemented using Krayon. */
abstract class KanvasView(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val krayonCanvas = AndroidKanvas(context, null, scalingFactor = applyDimension(COMPLEX_UNIT_DIP, 1f, resources.displayMetrics))

    @SuppressLint("WrongCall") // false positive because our custom draw function shares the name `onDraw`
    final override fun onDraw(canvas: Canvas) {
        krayonCanvas.setCanvas(canvas)
        onDraw(krayonCanvas)
        krayonCanvas.setCanvas(null)
    }

    /**
     * Implement this to do your drawing. The canvas has been pre-scaled such that 1 unit equals 1dp.
     *
     * Like [View.onDraw], you should avoid allocation in this function.
     *
     * Unlike [View.onDraw], you should use [Kanvas.width] and [Kanvas.height] instead of the view's
     * width and height.
     */
    protected abstract fun onDraw(canvas: Kanvas)
}
