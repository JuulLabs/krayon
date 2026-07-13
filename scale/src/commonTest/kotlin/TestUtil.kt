package com.juul.krayon.scale

import kotlin.test.assertEquals

internal fun assertFloatsEqual(
    expected: List<Float>,
    actual: List<Float>,
    absoluteTolerance: Float = 1e-4f,
) {
    assertEquals(expected.size, actual.size, "Expected $expected but was $actual (size mismatch).")
    for (i in expected.indices) {
        assertEquals(expected[i], actual[i], absoluteTolerance, "Mismatch at index $i: expected $expected but was $actual.")
    }
}
