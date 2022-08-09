package com.juul.krayon.element.view

import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement

private external class ResizeObserver(
    callback: (entries: Array<ResizeObserverEntry>, observer: ResizeObserver) -> Unit,
) {
    fun observe(target: Element)
    fun unobserve(target: Element)
}

private external interface ResizeObserverEntry

private var adapters = mutableMapOf<HTMLCanvasElement, ElementViewAdapter<*>>()
private var observers = mutableMapOf<HTMLCanvasElement, ResizeObserver>()

public fun HTMLCanvasElement.attachAdapter(
    adapter: ElementViewAdapter<*>,
) {
    detachAdapter()
    val observer = ResizeObserver { _, _ ->
        width = offsetWidth
        height = offsetHeight
        adapter.onSizeChanged(width, height)
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
