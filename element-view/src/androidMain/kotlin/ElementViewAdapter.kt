package com.juul.krayon.element.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.TypedValue
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.UpdateElement
import com.juul.krayon.kanvas.AndroidKanvas
import com.juul.krayon.kanvas.ScaledIsPointInPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private val EMPTY_STATE = AdapterState<ElementView>(null, RootElement(), 0, 0)

public class ElementViewAdapter<T>(
    private val dataSource: Flow<T>,
    private val updater: UpdateElement<T>,
) {

    private val state: MutableStateFlow<AdapterState<ElementView>> = MutableStateFlow(EMPTY_STATE)

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

    internal fun onClick(x: Float, y: Float): Boolean {
        val state = state.value
        if (state.view != null) {
            val scalingFactor = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, state.view.resources.displayMetrics)
            val isPointInPath = ScaledIsPointInPath(scalingFactor)
            return state.root.onClick(isPointInPath, x, y)
        }
        return false
    }

    /** Enqueue rendering in a new scope. */
    internal fun onAttached(view: ElementView) {
        state.value = AdapterState(view, RootElement(), view.width, view.height)
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

    private fun launchRenderingIn(scope: CoroutineScope) {
        val buffer = MutableSharedFlow<Bitmap>(extraBufferCapacity = 4, onBufferOverflow = BufferOverflow.SUSPEND)
        val pool = BitmapPool()
        scope.launch(Dispatchers.Main.immediate) {
            buffer.collect { bitmap ->
                this@ElementViewAdapter.bitmap?.let { pool.release(it) }
                this@ElementViewAdapter.bitmap = bitmap
                state.value.view?.invalidate()
            }
        }
        scope.launch {
            state.collectLatest { state ->
                if (state.view == null) return@collectLatest
                if (state.width == 0 || state.height == 0) return@collectLatest
                val scalingFactor = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, state.view.resources.displayMetrics)
                dataSource.collect { data ->
                    updater.update(state.root, state.width / scalingFactor, state.height / scalingFactor, data)
                    val bitmap = pool.acquire(state.width, state.height)
                    state.root.draw(AndroidKanvas(state.view.context, Canvas(bitmap), scalingFactor))
                    buffer.emit(bitmap)
                }
            }
        }
    }
}
