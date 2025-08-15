package com.juul.krayon.compose

import androidx.compose.ui.graphics.nativeCanvas
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.monospace
import com.juul.krayon.kanvas.sansSerif
import com.juul.krayon.kanvas.serif
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.skia.Data
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.Typeface
import org.jetbrains.skia.impl.use
import org.jetbrains.skia.Font as SkiaFont
import org.jetbrains.skia.Paint as SkiaPaint

private val systemSerif by lazy {
    checkNotNull(FontMgr.default.legacyMakeTypeface(serif, FontStyle.NORMAL))
}
private val systemSansSerif by lazy {
    checkNotNull(FontMgr.default.legacyMakeTypeface(sansSerif, FontStyle.NORMAL))
}
private val systemMonospace by lazy {
    checkNotNull(FontMgr.default.legacyMakeTypeface(monospace, FontStyle.NORMAL))
}

private val _typefaces = atomic(persistentMapOf<String, Typeface>())
private val typefaces: ImmutableMap<String, Typeface> = _typefaces.value

internal actual fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text) {
    paint.toSkiaFont().use { font ->
        val canvas = kanvas.scope.drawContext.canvas.nativeCanvas
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
        } else {
            when (name) {
                serif -> return systemSerif
                monospace -> return systemMonospace
                sansSerif -> return systemSansSerif
            }
        }
    }
    return systemSansSerif
}

private fun Paint.Text.toSkiaFont(): SkiaFont =
    SkiaFont(font.getTypeface(), size)

private fun Paint.Text.toSkiaPaint(): SkiaPaint {
    val paint = SkiaPaint()
    paint.color = color.argb
    return paint
}
