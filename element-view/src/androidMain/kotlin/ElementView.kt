package com.juul.krayon.element.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.View
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.AndroidKanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import android.graphics.Paint as AndroidPaint

private val EMPTY_STATE = ElementView.Adapter.State(null, RootElement(), 0, 0)

public class ElementView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    /** Paint used when re-drawing a rendered bitmap in [onDraw]. */
    private val blitPaint = AndroidPaint()

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
        adapter?.onSizeChanged(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        val bitmap = adapter?.bitmap
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0f, 0f, blitPaint)
        }
    }

    public class Adapter<T>(
        private val dataSource: Flow<T>,
        private val updater: Updater<T>,
    ) {

        /** Android's Size was added in API 21, so do this to avoid bumping min version. */
        public data class State(val view: ElementView?, val root: RootElement, val width: Int, val height: Int)

        public fun interface Updater<T> {
            public fun update(root: RootElement, width: Float, height: Float, data: T)
        }

        private val state: MutableStateFlow<State> = MutableStateFlow(EMPTY_STATE)

        /** Coroutine scope rendering occurs on. This scope is live between calls to [onAttached] and [onDetached]. */
        private var renderScope: CoroutineScope? = null
            set(value) {
                if (field !== value) field?.cancel()
                field = value
            }

        /** The most recent bitmap to finish rendering, if any. */
        internal var bitmap: Bitmap? = null
            private set

        /**
         * The view changed size, so we should re-render the bitmap. When this happens, the RootElement
         * will be reset, so the render will have a clean slate.
         */
        internal fun onSizeChanged(width: Int, height: Int) {
            state.value = state.value.copy(root = RootElement(), width = width, height = height)
        }

        /**
         * Attach this to a view (which is itself attached to a window).
         * This will immediately render the current state if possible.
         */
        internal fun onAttached(view: ElementView) {
            state.value = State(view, RootElement(), view.width, view.height)
            renderScope = object : CoroutineScope {
                private val job = Job()
                override val coroutineContext: CoroutineContext get() = job
            }.also { launchRenderingIn(it) }
        }

        /** Detach this from a view (usually because the view was detached from a window). */
        internal fun onDetached() {
            renderScope = null
            state.value = EMPTY_STATE
        }

        private fun launchRenderingIn(scope: CoroutineScope) = scope.launch {
            state.collectLatest { state ->
                if (state.view == null) return@collectLatest
                if (state.width == 0 || state.height == 0) return@collectLatest
                dataSource.conflate().collect { data ->
                    val scalingFactor = applyDimension(COMPLEX_UNIT_DIP, 1f, state.view.resources.displayMetrics)
                    updater.update(state.root, state.width / scalingFactor, state.height / scalingFactor, data)
                    ensureActive() // bitmap creation is expensive, so let's just ensure we don't want to cancel first
                    val bitmap = Bitmap.createBitmap(state.width, state.height, Bitmap.Config.ARGB_8888)
                    state.root.draw(AndroidKanvas(state.view.context, Canvas(bitmap), scalingFactor))
                    withContext(Dispatchers.Main) {
                        this@Adapter.bitmap?.recycle()
                        this@Adapter.bitmap = bitmap
                        state.view.invalidate()
                    }
                }
            }
        }
    }
}
