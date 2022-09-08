package com.juul.krayon.element.view

import com.juul.krayon.element.RootElement

internal data class AdapterState<V : Any>(
    val view: V?,
    val root: RootElement,
    val width: Int,
    val height: Int,
    val scale: Float = 1f,
)
