package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas

public class RootElement : Element() {
    override fun <PAINT, PATH> applyTo(canvas: Kanvas<PAINT, PATH>) {
        descendents.forEach { it.applyTo(canvas) }
    }
}
