package com.juul.krayon.element.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.graphics.Paint as AndroidPaint

public class ElementView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    /** Paint used when re-drawing a rendered bitmap in [onDraw]. */
    private val blitPaint = AndroidPaint()

    /** The chart [ElementViewAdapter]. */
    var adapter: ElementViewAdapter<*>? = null
        set(value) {
            if (isAttachedToWindow) {
                field?.onDetached()
            }
            field = value
            if (isAttachedToWindow) {
                value?.onAttached(this)
            }
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        adapter?.onAttached(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapter?.onDetached()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        adapter?.onSizeChanged(w, h)
    }

    @SuppressLint("ClickableViewAccessibility") // Can't use recommended `performClick` because we need touch coordinates.
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            adapter?.onClick(event.x, event.y)
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        val bitmap = adapter?.bitmap
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0f, 0f, blitPaint)
        }
    }
}
