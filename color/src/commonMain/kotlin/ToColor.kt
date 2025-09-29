package com.juul.krayon.color

public fun String.toColor(): Color =
    when {
        this[0] == '#' -> parseHexNotation(this)
        else -> keywordMap[this.lowercase()]
    } ?: throw IllegalArgumentException("Unknown color: `$this`.")

public fun String.toColorOrNull(): Color? =
    try {
        toColor()
    } catch (_: Exception) {
        null
    }

private fun parseHexNotation(text: String): Color = when (text.length) {
    4 -> { // #rgb
        val r = text[1].digitToInt(HEX_BASE)
        val g = text[2].digitToInt(HEX_BASE)
        val b = text[3].digitToInt(HEX_BASE)
        Color(alpha = 0xff, red = (r shl 4) or r, green = (g shl 4) or g, blue = (b shl 4) or b)
    }

    5 -> { // #rgba
        val r = text[1].digitToInt(HEX_BASE)
        val g = text[2].digitToInt(HEX_BASE)
        val b = text[3].digitToInt(HEX_BASE)
        val a = text[4].digitToInt(HEX_BASE)
        Color(alpha = (a shl 4) or a, red = (r shl 4) or r, green = (g shl 4) or g, blue = (b shl 4) or b)
    }

    7 -> { // #rrggbb
        val r = (text[1].digitToInt(HEX_BASE) shl 4) or text[2].digitToInt(HEX_BASE)
        val g = (text[3].digitToInt(HEX_BASE) shl 4) or text[4].digitToInt(HEX_BASE)
        val b = (text[5].digitToInt(HEX_BASE) shl 4) or text[6].digitToInt(HEX_BASE)
        Color(alpha = 0xff, red = r, green = g, blue = b)
    }

    9 -> { // #rrggbbaa
        val r = (text[1].digitToInt(HEX_BASE) shl 4) or text[2].digitToInt(HEX_BASE)
        val g = (text[3].digitToInt(HEX_BASE) shl 4) or text[4].digitToInt(HEX_BASE)
        val b = (text[5].digitToInt(HEX_BASE) shl 4) or text[6].digitToInt(HEX_BASE)
        val a = (text[7].digitToInt(HEX_BASE) shl 4) or text[8].digitToInt(HEX_BASE)
        Color(alpha = a, red = r, green = g, blue = b)
    }

    else -> throw IllegalArgumentException("Incorrect length. Hex notation must contain exactly 3, 4, 6, or 8 hexadecimal characters.")
}
