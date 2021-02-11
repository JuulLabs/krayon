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

    /** When `true`, [onKrayonSetup] will be invoked before the next call to [onKrayonDraw]. */
    private var requireSetup = true

    /** When `true`, [onKrayonStateChange] will be invoked before the next call to [onKrayonDraw]. */
    private var requireStateChange = true

    @SuppressLint("DrawAllocation")
    final override fun onDraw(canvas: android.graphics.Canvas) {
        AndroidCanvas(canvas).withDpScale(resources.displayMetrics) {
            if (requireSetup) {
                requireSetup = false
                onKrayonSetup(this)
            }
            if (requireStateChange) {
                requireStateChange = false
                onKrayonStateChange(this)
            }
            onKrayonDraw(this)
        }
    }

    /**
     * Called before [onKrayonDraw] and [onKrayonStateChange] on the first call.
     *
     * Useful for paints/paths that are created once and never change.
     */
    protected open fun onKrayonSetup(canvas: Canvas<AndroidPaint, AndroidPath>) {}

    /**
     * Called before [onKrayonDraw] on the first draw, and after calls to [invalidateKrayonState].
     * [CanvasView] does not make assumptions on what constitutes a state change, so make sure to
     * override relevant events (such as [onSizeChanged], if your state changes based on view size).
     *
     * Useful for paints/paths that must change in response to state changes.
     */
    protected open fun onKrayonStateChange(canvas: Canvas<AndroidPaint, AndroidPath>) {}

    /**
     * Implement this to do your drawing. The canvas has been pre-scaled such that 1 unit equals 1dp.
     *
     * Like [View.onDraw], you should avoid allocation in this function. Use [onKrayonSetup] and
     * [onKrayonStateChange] to perform allocations in a controllable manner.
     *
     * Unlike [View.onDraw], you should use [Canvas.width] and [Canvas.height] instead of the view's
     * width and height.
     */
    protected abstract fun onKrayonDraw(canvas: Canvas<AndroidPaint, AndroidPath>)

    /**
     * Request a call to [onKrayonStateChange] before the next call to [onKrayonDraw]. Calling this
     * also [invalidate]s this view.
     */
    protected fun invalidateKrayonState() {
        requireStateChange = true
        invalidate()
    }
}
