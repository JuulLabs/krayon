package com.juul.krayon.shape

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

private const val TAU = (2 * PI).toFloat()

class PieTest {

    @Test
    fun pie_withSingleDatum_fillsFullCircle() {
        val slice = pie().invoke(1f).single()
        assertEquals(0f, slice.startAngle)
        assertEquals(TAU, slice.endAngle)
    }

    @Test
    fun pie_withMultipleDatums_accountsForValue() {
        val (first, second, third) = pie().invoke(1f, 2f, 3f)
        assertEquals(0f, first.startAngle)
        assertEquals(TAU / 6f, first.endAngle)
        assertEquals(TAU / 6f, second.startAngle)
        assertEquals(TAU / 2f, second.endAngle)
        assertEquals(TAU / 2f, third.startAngle)
        assertEquals(TAU, third.endAngle)
    }
}
