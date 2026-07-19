package com.juul.krayon.format

private const val ALIGN_CHARS = "<>=^"
private const val SIGN_CHARS = "+-( "

/**
 * A parsed format specifier following the mini-language
 * `[[fill]align][sign][symbol][0][width][,][.precision][~][type]`.
 *
 * See the equivalent [d3 type](https://github.com/d3/d3-format#formatSpecifier).
 */
public class FormatSpecifier(
    fill: String? = null,
    align: String? = null,
    sign: String? = null,
    symbol: String? = null,
    zero: Boolean = false,
    width: Int? = null,
    comma: Boolean = false,
    precision: Int? = null,
    trim: Boolean = false,
    type: String? = null,
) {
    public var fill: String = fill ?: " "
    public var align: String = align ?: ">"
    public var sign: String = sign ?: "-"
    public var symbol: String = symbol ?: ""
    public var zero: Boolean = zero
    public var width: Int? = width
    public var comma: Boolean = comma
    public var precision: Int? = precision
    public var trim: Boolean = trim
    public var type: String = type ?: ""

    override fun toString(): String = buildString {
        append(fill)
        append(align)
        append(sign)
        append(symbol)
        append(if (zero) "0" else "")
        append(width?.let { maxOf(1, it).toString() } ?: "")
        append(if (comma) "," else "")
        append(precision?.let { "." + maxOf(0, it) } ?: "")
        append(if (trim) "~" else "")
        append(type)
    }
}

/**
 * Parses a format [specifier] string following the grammar
 * `[[fill]align][sign][symbol][0][width][,][.precision][~][type]`.
 *
 * See the equivalent [d3 function](https://github.com/d3/d3-format#formatSpecifier).
 */
public fun formatSpecifier(specifier: String): FormatSpecifier {
    val length = specifier.length
    var index = 0

    var fill: String? = null
    var align: String? = null
    when {
        index + 1 < length && specifier[index + 1] in ALIGN_CHARS -> {
            fill = specifier[index].toString()
            align = specifier[index + 1].toString()
            index += 2
        }
        index < length && specifier[index] in ALIGN_CHARS -> {
            align = specifier[index].toString()
            index += 1
        }
    }

    var sign: String? = null
    if (index < length && specifier[index] in SIGN_CHARS) {
        sign = specifier[index].toString()
        index++
    }

    var symbol: String? = null
    if (index < length && (specifier[index] == '$' || specifier[index] == '#')) {
        symbol = specifier[index].toString()
        index++
    }

    var zero = false
    if (index < length && specifier[index] == '0') {
        zero = true
        index++
    }

    var width: Int? = null
    val widthStart = index
    while (index < length && specifier[index] in '0'..'9') index++
    if (index > widthStart) width = specifier.substring(widthStart, index).toInt()

    var comma = false
    if (index < length && specifier[index] == ',') {
        comma = true
        index++
    }

    var precision: Int? = null
    if (index < length && specifier[index] == '.') {
        val precisionStart = index + 1
        var precisionEnd = precisionStart
        while (precisionEnd < length && specifier[precisionEnd] in '0'..'9') precisionEnd++
        if (precisionEnd == precisionStart) throw IllegalArgumentException("invalid format: $specifier")
        precision = specifier.substring(precisionStart, precisionEnd).toInt()
        index = precisionEnd
    }

    var trim = false
    if (index < length && specifier[index] == '~') {
        trim = true
        index++
    }

    var type: String? = null
    if (index < length && isTypeCharacter(specifier[index])) {
        type = specifier[index].toString()
        index++
    }

    if (index != length) throw IllegalArgumentException("invalid format: $specifier")

    return FormatSpecifier(fill, align, sign, symbol, zero, width, comma, precision, trim, type)
}

private fun isTypeCharacter(character: Char): Boolean =
    character == '%' || character in 'a'..'z' || character in 'A'..'Z'
