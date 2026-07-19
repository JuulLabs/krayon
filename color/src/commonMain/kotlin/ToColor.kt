package com.juul.krayon.color

private const val HEX = "[a-f0-9]"

private val RGB = """($HEX)($HEX)($HEX)""".toRegex(RegexOption.IGNORE_CASE)
private val RGBA = """($HEX)($HEX)($HEX)($HEX)""".toRegex(RegexOption.IGNORE_CASE)
private val RRGGBB = """($HEX{2})($HEX{2})($HEX{2})""".toRegex(RegexOption.IGNORE_CASE)
private val RRGGBBAA = """($HEX{2})($HEX{2})($HEX{2})($HEX{2})""".toRegex(RegexOption.IGNORE_CASE)

public fun String.toColor(): Color =
    when {
        this.startsWith("#") -> parseHexNotation(this)
        else -> parseFunctionalNotation(this) ?: keywordMap[this.lowercase()]
    } ?: throw IllegalArgumentException("Unknown color: `$this`.")

public fun String.toColorOrNull(): Color? =
    try {
        toColor()
    } catch (e: IllegalArgumentException) {
        null
    }

private fun parseHexNotation(text: String): Color {
    require(text.startsWith("#")) { "Hex notation must start with a pound sign (#)." }
    val hexText = text.substring(1)
    val match = when (hexText.length) {
        3 -> RGB.matchEntire(hexText)
        4 -> RGBA.matchEntire(hexText)
        6 -> RRGGBB.matchEntire(hexText)
        8 -> RRGGBBAA.matchEntire(hexText)
        else -> throw IllegalArgumentException("Incorrect length. Hex notation must contain exactly 3, 4, 6, or 8 hexadecimal characters.")
    } ?: throw IllegalArgumentException("Text is not a hexadecimal string. Each character must match the regex [a-fA-F0-9].")
    val red = match.groupValues[1]
    val green = match.groupValues[2]
    val blue = match.groupValues[3]
    val alpha = match.groupValues.getOrNull(4) ?: "ff"
    return Color(parseHexComponent(alpha), parseHexComponent(red), parseHexComponent(green), parseHexComponent(blue))
}

private const val NUMBER = """[-+]?\d*\.?\d+(?:[eE][-+]?\d+)?"""

private val RGB_INTEGER = """rgb\(\s*($NUMBER)\s*,\s*($NUMBER)\s*,\s*($NUMBER)\s*\)""".toRegex(RegexOption.IGNORE_CASE)
private val RGB_PERCENT = """rgb\(\s*($NUMBER)%\s*,\s*($NUMBER)%\s*,\s*($NUMBER)%\s*\)""".toRegex(RegexOption.IGNORE_CASE)
private val RGBA_INTEGER = """rgba\(\s*($NUMBER)\s*,\s*($NUMBER)\s*,\s*($NUMBER)\s*,\s*($NUMBER)\s*\)""".toRegex(RegexOption.IGNORE_CASE)
private val RGBA_PERCENT = """rgba\(\s*($NUMBER)%\s*,\s*($NUMBER)%\s*,\s*($NUMBER)%\s*,\s*($NUMBER)\s*\)""".toRegex(RegexOption.IGNORE_CASE)
private val HSL_PERCENT = """hsl\(\s*($NUMBER)\s*,\s*($NUMBER)%\s*,\s*($NUMBER)%\s*\)""".toRegex(RegexOption.IGNORE_CASE)
private val HSLA_PERCENT = """hsla\(\s*($NUMBER)\s*,\s*($NUMBER)%\s*,\s*($NUMBER)%\s*,\s*($NUMBER)\s*\)""".toRegex(RegexOption.IGNORE_CASE)

private fun parseFunctionalNotation(text: String): Color? {
    val trimmed = text.trim()
    RGB_INTEGER.matchEntire(trimmed)?.let { m ->
        return rgbColor(m.channel(1), m.channel(2), m.channel(3), 1f)
    }
    RGB_PERCENT.matchEntire(trimmed)?.let { m ->
        return rgbColor(m.percent(1), m.percent(2), m.percent(3), 1f)
    }
    RGBA_INTEGER.matchEntire(trimmed)?.let { m ->
        return rgbColor(m.channel(1), m.channel(2), m.channel(3), m.alpha(4))
    }
    RGBA_PERCENT.matchEntire(trimmed)?.let { m ->
        return rgbColor(m.percent(1), m.percent(2), m.percent(3), m.alpha(4))
    }
    HSL_PERCENT.matchEntire(trimmed)?.let { m ->
        return Hsl(m.number(1), m.fraction(2), m.fraction(3), 1f).toColor()
    }
    HSLA_PERCENT.matchEntire(trimmed)?.let { m ->
        return Hsl(m.number(1), m.fraction(2), m.fraction(3), m.alpha(4)).toColor()
    }
    return null
}

private fun MatchResult.number(group: Int): Float = groupValues[group].toFloat()

private fun MatchResult.channel(group: Int): Float = number(group)

private fun MatchResult.percent(group: Int): Float = number(group) / 100f * 255f

private fun MatchResult.fraction(group: Int): Float = number(group) / 100f

private fun MatchResult.alpha(group: Int): Float = number(group)

private fun rgbColor(red: Float, green: Float, blue: Float, alpha: Float): Color = Color(
    alpha = alpha.coerceIn(0f, 1f),
    red = (red / 255f).coerceIn(0f, 1f),
    green = (green / 255f).coerceIn(0f, 1f),
    blue = (blue / 255f).coerceIn(0f, 1f),
)

private fun parseHexComponent(component: String): Int =
    if (component.length == 1) {
        // Per MDN, "The three-digit notation (#RGB) is a shorter version of the six-digit form (#RRGGBB)."
        // Achieve that by just calling this function again with the component repeated.
        parseHexComponent(component + component)
    } else {
        component.toInt(HEX_BASE)
    }
