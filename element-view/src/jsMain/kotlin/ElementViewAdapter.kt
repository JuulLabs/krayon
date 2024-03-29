package com.juul.krayon.element.view

import com.juul.krayon.element.RootElement
import com.juul.krayon.element.UpdateElement
import com.juul.krayon.kanvas.HtmlKanvas
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitAnimationFrame
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import kotlin.coroutines.CoroutineContext

private val EMPTY_STATE = AdapterState<HTMLCanvasElement>(null, RootElement(), 0, 0)

public class ElementViewAdapter<T>(
    private val dataSource: Flow<T>,
    private val updater: UpdateElement<T>,
) {

    private val state: MutableStateFlow<AdapterState<HTMLCanvasElement>> = MutableStateFlow(EMPTY_STATE)

    /** Coroutine scope rendering occurs on. This scope is live between calls to [onAttached] and [onDetached]. */
    private var renderScope: CoroutineScope? = null
        set(value) {
            if (field !== value) field?.cancel()
            field = value
        }

    /**
     * The view changed size, so we should re-render the bitmap. When this happens, the RootElement
     * will be reset, so the render will have a clean slate.
     */
    internal fun onSizeChanged(width: Int, height: Int, scale: Double) {
        state.value = state.value.copy(root = RootElement(), width = width, height = height, scale = scale.toFloat())
    }

    internal fun onClick(x: Float, y: Float) {
        val state = state.value
        val canvas = state.view ?: return
        val kanvas = HtmlKanvas(canvas)
        state.root.onClick(kanvas, x, y)
    }

    internal fun onHover(x: Float, y: Float) {
        val state = state.value
        val canvas = state.view ?: return
        val kanvas = HtmlKanvas(canvas)
        state.root.onHover(kanvas, x, y)
    }

    internal fun onHoverEnded() {
        val state = state.value
        state.root.onHoverEnded()
    }

    /** Enqueue rendering in a new scope. */
    internal fun onAttached(view: HTMLCanvasElement) {
        state.value = AdapterState(view, RootElement(), view.width, view.height)
        renderScope = object : CoroutineScope {
            private val job = Job()
            override val coroutineContext: CoroutineContext get() = job
        }.also { launchRenderingIn(it) }
    }

    /** Detach this from a view, usually because the HTML Canvas was removed from the DOM. */
    internal fun onDetached() {
        renderScope = null
        state.value = EMPTY_STATE
    }

    private fun launchRenderingIn(scope: CoroutineScope) {
        scope.launch {
            state.collectLatest { state ->
                if (state.view == null) return@collectLatest
                if (state.width == 0 || state.height == 0) return@collectLatest
                dataSource.collect { data ->
                    updater.update(state.root, state.width.toFloat(), state.height.toFloat(), data)
                    val canvas = HtmlKanvas(state.view, state.scale)
                    canvas.context.clearRect(0.0, 0.0, state.width.toDouble(), state.height.toDouble())
                    state.root.draw(canvas)
                    canvas.context.resetTransform()
                    window.awaitAnimationFrame()
                }
            }
        }
    }
}
