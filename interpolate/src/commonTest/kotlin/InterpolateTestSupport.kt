package com.juul.krayon.interpolate

import com.juul.krayon.color.Color
import com.juul.krayon.color.toColor
import kotlin.math.abs
import kotlin.test.assertTrue

internal fun hex(value: String): Color = value.toColor()

internal fun assertColorClose(expected: Color, actual: Color, tolerance: Int = 1) {
    assertTrue(
        abs(expected.alpha - actual.alpha) <= tolerance &&
            abs(expected.red - actual.red) <= tolerance &&
            abs(expected.green - actual.green) <= tolerance &&
            abs(expected.blue - actual.blue) <= tolerance,
        "Expected ${expected.toHexString()} but was ${actual.toHexString()} (tolerance $tolerance).",
    )
}
