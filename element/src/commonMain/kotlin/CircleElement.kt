package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint

public class CircleElement(
    public var centerX: Float,
    public var centerY: Float,
    public var radius: Float,
    public var paint: Paint,
) : Element() {
    override fun <PAINT, PATH> applyTo(canvas: Kanvas<PAINT, PATH>) {
        canvas.drawCircle(centerX, centerY, radius, canvas.buildPaint(paint))
    }
}
