package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.withTransform

public class GroupElement(
    public var transform: Transform
) : Element() {

    override fun <PAINT, PATH> applyTo(canvas: Kanvas<PAINT, PATH>) {
        canvas.withTransform(transform) {
            descendents.forEach { it.applyTo(canvas) }
        }
    }
}
