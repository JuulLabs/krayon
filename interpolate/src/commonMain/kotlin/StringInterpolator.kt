package com.juul.krayon.interpolate

import kotlin.math.abs
import kotlin.math.floor

private val NUMBER = Regex("""[-+]?(?:\d+\.?\d*|\.?\d+)(?:[eE][-+]?\d+)?""")

/**
 * Interpolates the numbers embedded in [start] and [stop] while keeping the surrounding text of
 * [stop]. Numbers are paired positionally; static text between them is preserved. Mirrors d3's
 * `interpolateString`.
 */
public fun interpolateString(start: String, stop: String): Interpolator<String> {
    val segments = mutableListOf<String?>()
    val numbers = mutableListOf<Indexed>()
    var last = -1
    var consumed = 0

    fun appendStatic(text: String) {
        if (last >= 0 && segments[last] != null) {
            segments[last] = segments[last] + text
        } else {
            segments.add(text)
            last = segments.lastIndex
        }
    }

    val aMatches = NUMBER.findAll(start).iterator()
    val bMatches = NUMBER.findAll(stop).iterator()
    while (aMatches.hasNext() && bMatches.hasNext()) {
        val am = aMatches.next()
        val bm = bMatches.next()
        if (bm.range.first > consumed) {
            appendStatic(stop.substring(consumed, bm.range.first))
        }
        val aValue = am.value
        val bValue = bm.value
        if (aValue == bValue) {
            appendStatic(bValue)
        } else {
            segments.add(null)
            last = segments.lastIndex
            numbers.add(Indexed(last, interpolateChannel(aValue.toFloat(), bValue.toFloat())))
        }
        consumed = bm.range.last + 1
    }
    if (consumed < stop.length) {
        appendStatic(stop.substring(consumed))
    }

    return when {
        segments.size < 2 -> {
            if (numbers.isNotEmpty()) {
                val x = numbers[0].interpolator
                FunctionInterpolator { t -> formatNumber(x(t)) }
            } else {
                FunctionInterpolator { stop }
            }
        }
        else -> FunctionInterpolator { t ->
            for (indexed in numbers) segments[indexed.index] = formatNumber(indexed.interpolator(t))
            segments.joinToString("") { it ?: "" }
        }
    }
}

private class Indexed(val index: Int, val interpolator: (Float) -> Float)

private fun formatNumber(value: Float): String =
    if (value.isFinite() && value == floor(value) && abs(value) < 1e15f) {
        value.toLong().toString()
    } else {
        value.toString()
    }
