package com.juul.krayon.element

import com.juul.krayon.color.black
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint

public class RectangleElement(
    public var left: Float = 0f,
    public var top: Float = 0f,
    public var right: Float = 0f,
    public var bottom: Float = 0f,
    public var paint: Paint = Paint.Fill(black),
) : Element() {

    override fun <PAINT, PATH> applyTo(canvas: Kanvas<PAINT, PATH>) {
        canvas.drawRect(left, top, right, bottom, canvas.buildPaint(paint))
    }

    public companion object : TypeSelector<RectangleElement> {
        override fun trySelect(element: Element): RectangleElement? = element as? RectangleElement
    }
}
