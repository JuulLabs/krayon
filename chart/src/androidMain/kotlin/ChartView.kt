package com.juul.krayon.chart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.View
import com.juul.krayon.canvas.AndroidCanvas
import com.juul.krayon.chart.data.DataSet
import com.juul.krayon.chart.render.Renderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/** Displays charts! */
public class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    /** Paint used when re-drawing a rendered bitmap in [onDraw]. */
    private val blitPaint = Paint()

    /** The chart [Adapter]. */
    var adapter: Adapter<*>? = null
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
        adapter?.onSizeChanged()
    }

    override fun onDraw(canvas: Canvas) {
        val bitmap = adapter?.bitmap
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0f, 0f, blitPaint)
        }
    }

    /**
     * Adapter for the [ChartView]. Ensures that [Renderer] and [DataSet] types line up,
     * and handles rendering on background threads in response to various changes. Each
     * [Adapter] should only be used for a single [ChartView].
     *
     * If a render is under way when a state change is requested, the current render
     * will be canceled and a new one will start. This means the current approach is
     * not particularly animation friendly. In the future I'll see if I can improve this.
     */
    class Adapter<DATA> where DATA : DataSet<*> {

        /** The chart renderer. */
        var renderer: Renderer<DATA>? = null
            set(value) {
                field = value
                tryStartRender()
            }

        /** The data set to render. */
        var dataSet: DATA? = null
            set(value) {
                field = value
                tryStartRender()
            }

        /** Coroutine scope rendering occurs on. This scope is live between calls to [onAttached] and [onDetached]. */
        private var renderScope: CoroutineScope? = null

        /** View that this is attached to, if any. */
        private var attachedView: ChartView? = null

        /** The most recent bitmap to finish rendering, if any. */
        internal var bitmap: Bitmap? = null
            private set

        /**
         * Attach this to a view (which is itself attached to a window).
         * This will immediately render the current state if possible.
         */
        internal fun onAttached(view: ChartView) {
            attachedView = view
            renderScope = object : CoroutineScope {
                val job = Job()
                override val coroutineContext: CoroutineContext
                    get() = job
            }
            tryStartRender()
        }

        /** Detach this from a view (usually because the view was detached from a window). */
        internal fun onDetached() {
            renderScope?.cancel()
            renderScope = null
            attachedView = null
        }

        /** The view changed size, so we should re-render the bitmap. */
        internal fun onSizeChanged() {
            tryStartRender()
        }

        /** Store state as local variables (to protect from changes during render), and launch a render if everything is looking good. */
        private fun tryStartRender() {
            val renderer = this.renderer
            val dataSet = this.dataSet
            val renderScope = this.renderScope
            val attachedView = this.attachedView
            if (renderer != null && dataSet != null && renderScope != null && attachedView != null) {
                renderScope.coroutineContext[Job]?.cancelChildren()
                renderScope.launchRender(attachedView, renderer, dataSet)
            }
        }

        /** Re-render the chart. When rendering finishes, update [bitmap] and [invalidate] the attached view. */
        private fun CoroutineScope.launchRender(attachedView: ChartView, renderer: Renderer<DATA>, dataSet: DATA) {
            val width = attachedView.width
            val height = attachedView.height
            val displayMetrics = attachedView.resources.displayMetrics
            if (width > 0 && height > 0) {
                launch {
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = AndroidCanvas(Canvas(bitmap), scalingFactor = applyDimension(COMPLEX_UNIT_DIP, 1f, displayMetrics))
                    renderer.render(dataSet, canvas)
                    (this@Adapter).bitmap = bitmap
                    withContext(Dispatchers.Main) {
                        attachedView.invalidate()
                    }
                }
            }
        }
    }
}
