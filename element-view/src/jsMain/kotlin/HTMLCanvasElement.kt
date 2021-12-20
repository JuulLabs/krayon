package com.juul.krayon.element.view

import org.w3c.dom.HTMLCanvasElement

private var adapters = mutableMapOf<HTMLCanvasElement, ElementViewAdapter<*>>()

public fun HTMLCanvasElement.attachAdapter(
    adapter: ElementViewAdapter<*>
) {
    detachAdapter()
    adapters[this] = adapter
    adapter.onAttached(this)
    onresize = { adapter.onSizeChanged(width, height) }
}

public fun HTMLCanvasElement.detachAdapter() {
    adapters.remove(this)?.onDetached()
    onresize = null
}
