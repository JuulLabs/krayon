package com.juul.krayon.compose

import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.util.fastRoundToInt
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.sansSerif
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.skia.Data
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.Typeface
import org.jetbrains.skia.Font as SkiaFont
import org.jetbrains.skia.Paint as SkiaPaint

private val defaultTypeface by lazy {
    FontMgr.default.legacyMakeTypeface(sansSerif, FontStyle.NORMAL)!!
}

private val _typefaces = MutableStateFlow(persistentMapOf<String, Typeface>())
private val typefaces: ImmutableMap<String, Typeface> get() = _typefaces.value

private val _fonts = MutableStateFlow(persistentMapOf<Pair<Typeface, Float>, SkiaFont>())
internal val fonts: ImmutableMap<Pair<Typeface, Float>, SkiaFont> get() = _fonts.value

internal actual fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text) {
    val canvas = kanvas.scope.drawContext.canvas.nativeCanvas
    val font = paint.toSkiaFont()
    val string = text.toString()
    canvas.drawString(
        string,
        x = when (paint.alignment) {
            Paint.Text.Alignment.Left -> x
            Paint.Text.Alignment.Center -> x - (0.5f * font.measureTextWidth(string))
            Paint.Text.Alignment.Right -> x - font.measureTextWidth(string)
        },
        y = y,
        font = font,
        paint = paint.toSkiaPaint(),
    )
}

private fun Font.getTypeface(): Typeface {
    for (name in names) {
        val existing = typefaces[name]
        if (existing != null) {
            return existing
        }

        val association = fontAssociations[name]
        if (association != null) {
            val data = Data.makeFromBytes(association)
            val typeface = FontMgr.default.makeFromData(data)
            if (typeface != null) {
                _typefaces.update { it.put(name, typeface) }
                return typeface
            }
        }
    }
    return defaultTypeface
}

private fun Paint.Text.toSkiaFont(): SkiaFont {
    val typeface = font.getTypeface()
    val quantizedSize = (size * 8f).fastRoundToInt() * 0.125f
    val key = typeface to quantizedSize
    val existing = fonts[key]
    if (existing != null) {
        return existing
    }
    val font = SkiaFont(font.getTypeface(), quantizedSize)
    _fonts.update { it.put(key, font) }
    return font
}

private fun Paint.Text.toSkiaPaint(): SkiaPaint {
    val paint = SkiaPaint()
    paint.color = color.argb
    return paint
}
