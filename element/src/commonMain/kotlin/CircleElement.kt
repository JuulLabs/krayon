package com.juul.krayon.element

import com.juul.krayon.color.black
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint

public class CircleElement(
    public var centerX: Float = 0f,
    public var centerY: Float = 0f,
    public var radius: Float = 0f,
    public var paint: Paint = Paint.Fill(black),
) : Element() {

    override fun <PAINT, PATH> applyTo(canvas: Kanvas<PAINT, PATH>) {
        canvas.drawCircle(centerX, centerY, radius, canvas.buildPaint(paint))
    }

    public companion object : TypeSelector<CircleElement> {
        override fun trySelect(element: Element): CircleElement? = element as? CircleElement
    }
}
