package com.juul.krayon.element.view

import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.roundToInt

private external class ResizeObserver(
    callback: (entries: Array<ResizeObserverEntry>, observer: ResizeObserver) -> Unit,
) {
    fun observe(target: Element)
    fun unobserve(target: Element)
}

private external interface ResizeObserverEntry

private var adapters = mutableMapOf<HTMLCanvasElement, ElementViewAdapter<*>>()
private var observers = mutableMapOf<HTMLCanvasElement, ResizeObserver>()

/**
 * Attach an [adapter] to `this` element. When [applyDisplayScale] is `true` (the default), then
 * the element will properly handle scaled high-DPI devices. This can be set to `false` to prevent
 * an infinite resize loop when the element's size is defined intrinsically by the canvas size.
 */
public fun HTMLCanvasElement.attachAdapter(
    adapter: ElementViewAdapter<*>,
    applyDisplayScale: Boolean = true,
) {
    detachAdapter()
    val observer = ResizeObserver { _, _ ->
        val scale = if (applyDisplayScale) window.devicePixelRatio else 1.0
        width = (offsetWidth * scale).roundToInt()
        height = (offsetHeight * scale).roundToInt()
        adapter.onSizeChanged(offsetWidth, offsetHeight, scale)
    }
    observers[this] = observer
    observer.observe(this)
    adapters[this] = adapter
    adapter.onAttached(this)
    onclick = { adapter.onClick(it.offsetX.toFloat(), it.offsetY.toFloat()) }
    onmouseover = { adapter.onHover(it.offsetX.toFloat(), it.offsetY.toFloat()) }
    onmousemove = { adapter.onHover(it.offsetX.toFloat(), it.offsetY.toFloat()) }
    onmouseleave = { adapter.onHoverEnded() }
}

public fun HTMLCanvasElement.detachAdapter() {
    observers.remove(this)?.unobserve(this)
    adapters.remove(this)?.onDetached()
    onclick = null
    onmouseover = null
    onmousemove = null
    onmouseleave = null
}
