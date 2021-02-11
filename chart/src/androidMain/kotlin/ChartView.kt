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

public class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val blitPaint = Paint()

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

    class Adapter<DATA> where DATA : DataSet<*> {

        var renderer: Renderer<DATA>? = null
            set(value) {
                field = value
                tryStartRender()
            }

        var dataSet: DATA? = null
            set(value) {
                field = value
                tryStartRender()
            }

        private var renderScope: CoroutineScope? = null
        private var attachedView: ChartView? = null

        internal var bitmap: Bitmap? = null
            private set

        internal fun onAttached(view: ChartView) {
            attachedView = view
            renderScope = object : CoroutineScope {
                val job = Job()
                override val coroutineContext: CoroutineContext
                    get() = job
            }
            tryStartRender()
        }

        internal fun onDetached() {
            renderScope?.cancel()
            renderScope = null
            attachedView = null
        }

        internal fun onSizeChanged() {
            tryStartRender()
        }

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
