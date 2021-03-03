package com.juul.krayon.kanvas.svg

import com.juul.krayon.kanvas.Color
import com.juul.krayon.kanvas.DEFAULT_MITER_LIMIT
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.xml.XmlElement
import com.juul.krayon.kanvas.xml.escape

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
    if (cap != "butt") {
        // SVG stroke line cap defaults to "butt"; set the attribute only when the specified value is not the default.
        setAttribute("stroke-linecap", cap)
    }
    val join = when (paint.join) {
        Paint.Stroke.Join.Bevel -> "bevel"
        Paint.Stroke.Join.Round -> "round"
        is Paint.Stroke.Join.Miter -> {
            if (paint.join.limit != DEFAULT_MITER_LIMIT) {
                // SVG stroke miter limit defaults to 4; set the attribute only when the specified value is not the default.
                setAttribute("stroke-miterlimit", paint.join.limit)
            }
            "miter"
        }
    }
    if (join != "miter") {
        // SVG stroke line join defaults to "miter"; set the attribute only when the specified value is not the default.
        setAttribute("stroke-linejoin", join)
    }
    if (paint.dash is Paint.Stroke.Dash.Pattern) {
        setAttribute("stroke-dasharray", paint.dash.intervals.joinToString(separator = " ") { it.toDouble().toString() })
    }
    setAttribute("stroke-width", "${paint.width.toDouble()}px")
    setColorAttributes("stroke", paint.color)
    // SVG defaults to a black fill. Explicitly set it as "none" since this is a stroke-only paint.
    setAttribute("fill", "none")
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
