package com.juul.krayon.format

import kotlin.math.pow

private val siPrefixes =
    listOf("y", "z", "a", "f", "p", "n", "\u00b5", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y")

private val knownTypes = setOf("e", "f", "g", "r", "s", "%", "p", "b", "o", "d", "x", "X", "c")
private val radixTypes = setOf("b", "o", "x", "X")

/**
 * The locale-specific definitions used to build a [FormatLocale].
 *
 * See the equivalent [d3 definition](https://github.com/d3/d3-format#locale_format).
 */
public data class FormatLocaleDefinition(
    val decimal: String = ".",
    val thousands: String? = null,
    val grouping: List<Int>? = null,
    val currencyPrefix: String = "",
    val currencySuffix: String = "",
    val numerals: List<String>? = null,
    val percent: String = "%",
    val minus: String = "\u2212",
    val nan: String = "NaN",
)

/** A number formatter bound to a [FormatSpecifier] and its [FormatLocale]. Invoke it with a value to format. */
public class NumberFormat internal constructor(
    private val specifier: FormatSpecifier,
    private val delegate: (Double) -> String,
) {
    public operator fun invoke(value: Double): String = delegate(value)

    override fun toString(): String = specifier.toString()
}

/**
 * A locale-aware factory for number formatters.
 *
 * See the equivalent [d3 factory](https://github.com/d3/d3-format#formatLocale).
 */
public class FormatLocale(definition: FormatLocaleDefinition) {

    private val decimal = definition.decimal
    private val thousands = definition.thousands
    private val grouping = definition.grouping
    private val currencyPrefix = definition.currencyPrefix
    private val currencySuffix = definition.currencySuffix
    private val numerals = definition.numerals
    private val percent = definition.percent
    private val minus = definition.minus
    private val nan = definition.nan

    /** Builds a formatter from a [specifier] string. See [d3](https://github.com/d3/d3-format#locale_format). */
    public fun format(specifier: String): NumberFormat = format(formatSpecifier(specifier))

    /** Builds a formatter from a parsed [specifier]. */
    public fun format(specifier: FormatSpecifier): NumberFormat = build(specifier, "", "")

    /**
     * Builds a formatter that always uses the SI prefix appropriate for [value].
     *
     * See the equivalent [d3 function](https://github.com/d3/d3-format#locale_formatPrefix).
     */
    public fun formatPrefix(specifier: String, value: Double): NumberFormat {
        val parsed = formatSpecifier(specifier)
        parsed.type = "f"
        val exponent = maxOf(-8, minOf(8, (exponent10(value) ?: 0).floorDiv(3))) * 3
        val factor = 10.0.pow(-exponent)
        val prefixSuffix = siPrefixes[8 + exponent / 3]
        val inner = build(parsed, "", prefixSuffix)
        return NumberFormat(parsed) { inner(factor * it) }
    }

    private fun build(specifier: FormatSpecifier, optionPrefix: String, optionSuffix: String): NumberFormat {
        val fillInput = specifier.fill
        val alignInput = specifier.align
        val sign = specifier.sign
        val symbol = specifier.symbol
        val width = specifier.width
        var comma = specifier.comma
        var precisionInput = specifier.precision
        var trim = specifier.trim
        var type = specifier.type

        if (type == "n") {
            comma = true
            type = "g"
        } else if (type !in knownTypes) {
            if (precisionInput == null) precisionInput = 12
            trim = true
            type = "g"
        }

        var zero = specifier.zero
        var fill = fillInput
        var align = alignInput
        if (zero || (fill == "0" && align == "=")) {
            zero = true
            fill = "0"
            align = "="
        }

        val symbolPrefix = when {
            symbol == "$" -> currencyPrefix
            symbol == "#" && type in radixTypes -> "0" + type.lowercase()
            else -> ""
        }
        val symbolSuffix = when {
            symbol == "$" -> currencySuffix
            type == "%" || type == "p" -> percent
            else -> ""
        }
        val basePrefix = optionPrefix + symbolPrefix
        val baseSuffix = symbolSuffix + optionSuffix

        val maybeSuffix = type in setOf("d", "e", "f", "g", "p", "r", "s", "%")

        val precision = when {
            precisionInput == null -> 6
            type == "g" || type == "p" || type == "r" || type == "s" -> maxOf(1, minOf(21, precisionInput))
            else -> maxOf(0, minOf(20, precisionInput))
        }

        val delegate: (Double) -> String = format@{ value ->
            var valuePrefix = basePrefix
            var valueSuffix = baseSuffix
            var body: String

            if (type == "c") {
                valueSuffix = jsNumberToString(value) + valueSuffix
                body = ""
            } else {
                var negative = value < 0.0 || (value == 0.0 && 1.0 / value < 0.0)
                var prefixExponent: Int? = null
                if (value.isNaN()) {
                    body = nan
                } else {
                    val result = applyType(type, kotlin.math.abs(value), precision)
                    body = result.first
                    prefixExponent = result.second
                }
                if (trim) body = formatTrim(body)
                if (negative && (body.toDoubleOrNull() == 0.0) && sign != "+") negative = false

                valuePrefix = (
                    if (negative) {
                        if (sign == "(") "(" else minus
                    } else {
                        if (sign == "-" || sign == "(") "" else sign
                    }
                ) + valuePrefix

                val siSuffix = if (type == "s" && prefixExponent != null) siPrefixes[8 + prefixExponent / 3] else ""
                valueSuffix = siSuffix + valueSuffix + (if (negative && sign == "(") ")" else "")

                if (maybeSuffix) {
                    var i = 0
                    while (i < body.length) {
                        val code = body[i].code
                        if (code < 48 || code > 57) {
                            valueSuffix = (if (code == 46) decimal + body.substring(i + 1) else body.substring(i)) + valueSuffix
                            body = body.substring(0, i)
                            break
                        }
                        i++
                    }
                }
            }

            if (comma && !zero) body = group(body, Int.MAX_VALUE)

            val length = valuePrefix.length + body.length + valueSuffix.length
            var padding = if (width != null && length < width) fill.repeat(width - length) else ""

            if (comma && zero) {
                body = group(
                    padding + body,
                    if (padding.isNotEmpty() && width != null) width - valueSuffix.length else Int.MAX_VALUE,
                )
                padding = ""
            }

            val assembled = when (align) {
                "<" -> valuePrefix + body + valueSuffix + padding
                "=" -> valuePrefix + padding + body + valueSuffix
                "^" -> {
                    val half = padding.length shr 1
                    padding.substring(0, half) + valuePrefix + body + valueSuffix + padding.substring(half)
                }
                else -> padding + valuePrefix + body + valueSuffix
            }

            applyNumerals(assembled)
        }

        return NumberFormat(specifier, delegate)
    }

    private fun applyType(type: String, absValue: Double, precision: Int): Pair<String, Int?> = when (type) {
        "e" -> formatExponential(absValue, precision) to null
        "f" -> formatFixed(absValue, precision) to null
        "g" -> formatPrecision(absValue, precision) to null
        "r" -> formatRounded(absValue, precision) to null
        "s" -> formatPrefixAuto(absValue, precision)
        "%" -> formatFixed(absValue * 100.0, precision) to null
        "p" -> formatRounded(absValue * 100.0, precision) to null
        "b" -> formatRadix(absValue, 2) to null
        "o" -> formatRadix(absValue, 8) to null
        "d" -> formatDecimalInteger(absValue) to null
        "x" -> formatRadix(absValue, 16) to null
        "X" -> formatRadix(absValue, 16).uppercase() to null
        else -> jsNumberToString(absValue) to null
    }

    private fun group(value: String, width: Int): String {
        val grouping = grouping
        val thousands = thousands
        if (grouping.isNullOrEmpty() || thousands == null) return value
        val chunks = ArrayList<String>()
        var index = value.length
        var groupIndex = 0
        var groupSize = grouping[0]
        var consumed = 0
        while (index > 0 && groupSize > 0) {
            if (consumed + groupSize + 1 > width) groupSize = maxOf(1, width - consumed)
            val end = index
            index -= groupSize
            val start = if (index < 0) 0 else index
            chunks.add(value.substring(start, end))
            consumed += groupSize + 1
            if (consumed > width) break
            groupIndex = (groupIndex + 1) % grouping.size
            groupSize = grouping[groupIndex]
        }
        chunks.reverse()
        return chunks.joinToString(separator = thousands)
    }

    private fun applyNumerals(value: String): String {
        val numerals = numerals ?: return value
        return buildString {
            for (character in value) {
                append(if (character in '0'..'9') numerals[character - '0'] else character.toString())
            }
        }
    }
}

internal fun formatTrim(text: String): String {
    var trimStart = -1
    var trimEnd = 0
    var i = 1
    while (i < text.length) {
        when (text[i]) {
            '.' -> {
                trimStart = i
                trimEnd = i
            }
            '0' -> {
                if (trimStart == 0) trimStart = i
                trimEnd = i
            }
            in '1'..'9' -> if (trimStart > 0) trimStart = 0
            else -> break
        }
        i++
    }
    return if (trimStart > 0) text.substring(0, trimStart) + text.substring(trimEnd + 1) else text
}
