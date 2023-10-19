package com.juul.krayon.element.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_HOVER_ENTER
import android.view.MotionEvent.ACTION_HOVER_EXIT
import android.view.MotionEvent.ACTION_HOVER_MOVE
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.MotionEvent.TOOL_TYPE_FINGER
import android.view.View
import android.graphics.Paint as AndroidPaint

public class ElementView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : View(context, attrs) {

        /** Paint used when re-drawing a rendered bitmap in [onDraw]. */
        private val blitPaint = AndroidPaint()

        /** The chart [ElementViewAdapter]. */
        public var adapter: ElementViewAdapter<*>? = null
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
            val bounds = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
            if (!bounds.contains(event.x, event.y)) {
                // We're receiving a touch event that's off of our bounds. This means that the user has
                // dragged until they are no longer on the view.
                adapter?.onHoverEnded()
                return false
            }

            when (event.actionMasked) {
                ACTION_DOWN, ACTION_MOVE -> {
                    adapter?.onHover(event.x, event.y)
                }
                ACTION_UP -> {
                    adapter?.onClick(event.x, event.y)
                    if (event.getToolType(0) == TOOL_TYPE_FINGER) {
                        // Touch loses hover when click ends, but other input types like mouse don't.
                        adapter?.onHoverEnded()
                    }
                }
                ACTION_CANCEL -> {
                    adapter?.onHoverEnded()
                }
            }
            return true
        }

        override fun onHoverEvent(event: MotionEvent): Boolean {
            when (event.actionMasked) {
                ACTION_HOVER_ENTER, ACTION_HOVER_MOVE -> adapter?.onHover(event.x, event.y)
                ACTION_HOVER_EXIT -> adapter?.onHoverEnded()
                // Should be unreachable, but if they add a new event in the future we shouldn't consume it.
                else -> return false
            }
            return true
        }

        override fun onDraw(canvas: Canvas) {
            val bitmap = adapter?.bitmap
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0f, 0f, blitPaint)
            }
        }
    }
