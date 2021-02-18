package com.juul.krayon.canvas.svg

import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.Paint
import com.juul.krayon.canvas.xml.XmlElement
import com.juul.krayon.canvas.xml.escape

internal fun XmlElement.setPaintAttributes(paint: Paint) = apply {
    when (paint) {
        is Paint.Stroke -> setStrokeAttributes(paint)
        is Paint.Fill -> setFillAttributes(paint)
        is Paint.Text -> setTextAttributes(paint)
    }
}

private fun XmlElement.setStrokeAttributes(paint: Paint.Stroke) = apply {
    val cap = when (paint.cap) {
        Paint.Stroke.Cap.Butt -> "butt"
        Paint.Stroke.Cap.Round -> "round"
        Paint.Stroke.Cap.Square -> "square"
    }
    if (cap != "butt") { // Don't bother writing the default
        setAttribute("stroke-linecap", cap)
    }
    val join = when (paint.join) {
        Paint.Stroke.Join.Bevel -> "bevel"
        Paint.Stroke.Join.Round -> "round"
        is Paint.Stroke.Join.Miter -> {
            val miterLimit: Double = TODO("See https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-miterlimit for the math")
            setAttribute("stroke-miterlimit", miterLimit)
            "miter"
        }
    }
    if (join != "miter") { // Don't bother writing the default
        setAttribute("stroke-linejoin", join)
    }
    setColorAttributes("stroke", paint.color)
    setAttribute("stroke-width", "${paint.width.toDouble()}px")
}

private fun XmlElement.setFillAttributes(paint: Paint.Fill) = apply {
    setColorAttributes("fill", paint.color)
}

private fun XmlElement.setTextAttributes(paint: Paint.Text) = apply {
    val anchor = when (paint.alignment) {
        Paint.Text.Alignment.Left -> "start"
        Paint.Text.Alignment.Center -> "middle"
        Paint.Text.Alignment.Right -> "end"
    }
    setAttribute("text-anchor", anchor)
    for (name in paint.font.names) {
        require(name.escape().toString() == name) { "Font names cannot contain characters that must be escaped." }
    }
    setAttribute("font-family", paint.font.names.joinToString { if (it.contains("""\s""".toRegex())) "\"$it\"" else it })
    setAttribute("font-size", "${paint.size.toDouble()}px")
    setColorAttributes("fill", paint.color)
}

private fun XmlElement.setColorAttributes(id: String, color: Color) = apply {
    setAttribute(id, "#${color.rgb.toString(16).padStart(6, '0')}")
    if (color.alpha != 0xFF) {
        setAttribute("$id-opacity", color.alpha / 255.0)
    }
}
