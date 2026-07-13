package com.juul.krayon.color

import kotlin.math.abs
import kotlin.test.assertTrue

/** Asserts that each channel of [actual] is within [tolerance] of [expected]. */
internal fun assertColorClose(expected: Color, actual: Color, tolerance: Int = 1) {
    assertTrue(
        abs(expected.alpha - actual.alpha) <= tolerance &&
            abs(expected.red - actual.red) <= tolerance &&
            abs(expected.green - actual.green) <= tolerance &&
            abs(expected.blue - actual.blue) <= tolerance,
        "Expected ${expected.toHexString()} but was ${actual.toHexString()} (tolerance $tolerance).",
    )
}
