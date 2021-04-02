package com.juul.krayon.color

private const val HEX = "[a-f0-9]"

private val RGB = """($HEX)($HEX)($HEX)""".toRegex(RegexOption.IGNORE_CASE)
private val RGBA = """($HEX)($HEX)($HEX)($HEX)""".toRegex(RegexOption.IGNORE_CASE)
private val RRGGBB = """($HEX{2})($HEX{2})($HEX{2})""".toRegex(RegexOption.IGNORE_CASE)
private val RRGGBBAA = """($HEX{2})($HEX{2})($HEX{2})($HEX{2})""".toRegex(RegexOption.IGNORE_CASE)

public fun String.toColor(): Color =
    keywordMap[this.toLowerCase()] ?: parseHexNotation(this)

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

private fun parseHexComponent(component: String): Int =
    if (component.length == 1) {
        // Per MDN, "The three-digit notation (#RGB) is a shorter version of the six-digit form (#RRGGBB)."
        // Achieve that by just calling this function again with the component repeated.
        parseHexComponent(component + component)
    } else {
        component.toInt(HEX_BASE)
    }
