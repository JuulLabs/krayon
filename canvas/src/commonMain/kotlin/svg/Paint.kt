package com.juul.krayon.canvas.svg

import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.Paint
import kotlin.math.roundToInt

// FIXME: This could be cleaned up a lot
internal fun Paint.toAttributeString(): String = when (this) {
    is Paint.Stroke -> buildString {
        appendColorAttributes("stroke", color)
        append("""stroke-width="${width.toDouble()}px" """)
        val cap = when (cap) {
            Paint.Stroke.Cap.Butt -> null // defaults to "butt"
            Paint.Stroke.Cap.Round -> "round"
            Paint.Stroke.Cap.Square -> "square"
        }
        if (cap != null) {
            append("""stroke-linecap="$cap" """)
        }
        if (join is Paint.Stroke.Join.Miter) {
            TODO("the math. SVG uses a weird non-angular format. See https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-miterlimit")
        }
        val join = when (join) {
            Paint.Stroke.Join.Bevel -> "bevel"
            Paint.Stroke.Join.Round -> "round"
            is Paint.Stroke.Join.Miter -> null // defaults to "miter"
        }
        if (join != null) {
            append("""stroke-linejoin="$join" """)
        }
    }
    is Paint.Fill -> buildString {
        appendColorAttributes("fill", color)
    }
    is Paint.Text -> buildString {
        val anchor = when (alignment) {
            Paint.Text.Alignment.Left -> "start"
            Paint.Text.Alignment.Center -> "middle"
            Paint.Text.Alignment.Right -> "end"
        }
        append("""text-anchor="$anchor" """)
        // STOPSHIP: text must be escaped or else we can break the SVG pretty easily
        append("""font-family="${font.names.joinToString { "\\\"$it\\\"" }}" """)
        append("""font-size="${size.toDouble()}px" """)
        appendColorAttributes("fill", color)
    }
}.trimEnd()

private fun StringBuilder.appendColorAttributes(attribute: String, color: Color) {
    append("""$attribute="${color.asSvgColor()}" """)
    if (color.alpha != 0xFF) {
        append("""$attribute-opacity="${color.asSvgOpacity()}" """)
    }
}

private fun Color.asSvgColor(): String = "#${rgb.toString(16)}"

// TODO: There's probably a way cleaner way to do this. The goal is just not using WAY more decimals than can be represented.
private fun Color.asSvgOpacity(): String = ((alpha / 0.0255).roundToInt() / 10000.0).toString()
