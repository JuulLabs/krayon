package com.juul.krayon.kanvas.svg

import com.juul.krayon.color.Color
import com.juul.krayon.kanvas.DEFAULT_MITER_LIMIT
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.xml.NumberFormatter
import com.juul.krayon.kanvas.xml.XmlElement
import com.juul.krayon.kanvas.xml.escape

internal fun XmlElement.setPaintAttributes(paint: Paint, formatter: NumberFormatter) = apply {
    when (paint) {
        is Paint.Fill -> setFillAttributes(paint, formatter)
        is Paint.Stroke -> setStrokeAttributes(paint, formatter)
        is Paint.FillAndStroke -> {
            setStrokeAttributes(paint.stroke, formatter)
            setFillAttributes(paint.fill, formatter)
        }
        is Paint.Text -> setTextAttributes(paint, formatter)
    }
}

private fun XmlElement.setStrokeAttributes(paint: Paint.Stroke, formatter: NumberFormatter) = apply {
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
                setAttribute("stroke-miterlimit", paint.join.limit, formatter)
            }
            "miter"
        }
    }
    if (join != "miter") {
        // SVG stroke line join defaults to "miter"; set the attribute only when the specified value is not the default.
        setAttribute("stroke-linejoin", join)
    }
    if (paint.dash is Paint.Stroke.Dash.Pattern) {
        setAttribute("stroke-dasharray", paint.dash.intervals.joinToString(separator = " ", transform = formatter::invoke))
    }
    setAttribute("stroke-width", "${formatter(paint.width)}px")
    setColorAttributes("stroke", paint.color, formatter)
    // SVG defaults to a black fill. Explicitly set it as "none" since this is a stroke-only paint.
    setAttribute("fill", "none")
}

private fun XmlElement.setFillAttributes(paint: Paint.Fill, formatter: NumberFormatter) = apply {
    setColorAttributes("fill", paint.color, formatter)
}

private fun XmlElement.setTextAttributes(paint: Paint.Text, formatter: NumberFormatter) = apply {
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
    setAttribute("font-size", "${formatter(paint.size)}px")
    setColorAttributes("fill", paint.color, formatter)
}

private fun XmlElement.setColorAttributes(id: String, color: Color, formatter: NumberFormatter) = apply {
    setAttribute(id, "#${color.rgb.toString(16).padStart(6, '0')}")
    if (color.alpha != 0xFF) {
        setAttribute("$id-opacity", color.alpha / 255.0, formatter)
    }
}
