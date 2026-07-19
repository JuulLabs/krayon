package com.juul.krayon.interpolate

import kotlin.test.Test
import kotlin.test.assertEquals

class GeneralInterpolatorTests {

    @Test
    fun interpolateRound_roundsToNearestInt() {
        assertEquals(6, interpolateRound(0f, 10f).interpolate(0.55f))
        assertEquals(0, interpolateRound(0f, 10f).interpolate(0f))
        assertEquals(10, interpolateRound(0f, 10f).interpolate(1f))
    }

    @Test
    fun interpolateString_embeddedNumbers_matchesD3() {
        assertEquals("10px", interpolateString("0px", "20px").interpolate(0.5f))
        assertEquals("M5,10", interpolateString("M0,0", "M10,20").interpolate(0.5f))
    }

    @Test
    fun interpolateString_withNoNumbers_returnsStop() {
        assertEquals("done", interpolateString("start", "done").interpolate(0.5f))
    }

    @Test
    fun interpolateBasis_matchesD3() {
        val interpolator = interpolateBasis(listOf(0f, 10f, 20f, 30f))
        assertEquals(0f, interpolator.interpolate(0f), 1e-4f)
        assertEquals(7.5f, interpolator.interpolate(0.25f), 1e-4f)
        assertEquals(15f, interpolator.interpolate(0.5f), 1e-4f)
        assertEquals(30f, interpolator.interpolate(1f), 1e-4f)
    }

    @Test
    fun interpolateBasisClosed_matchesD3() {
        val interpolator = interpolateBasisClosed(listOf(0f, 10f, 20f, 30f))
        assertEquals(6.66667f, interpolator.interpolate(0f), 1e-3f)
        assertEquals(10f, interpolator.interpolate(0.25f), 1e-3f)
        assertEquals(20f, interpolator.interpolate(0.5f), 1e-3f)
    }

    @Test
    fun piecewise_rgbAcrossThreeColors_matchesD3() {
        val interpolator = piecewise(listOf(hex("#ff0000"), hex("#008000"), hex("#0000ff"))) { a, b ->
            interpolateRgb(a, b)
        }
        assertColorClose(hex("#ff0000"), interpolator.interpolate(0f))
        assertColorClose(hex("#804000"), interpolator.interpolate(0.25f))
        assertColorClose(hex("#008000"), interpolator.interpolate(0.5f))
        assertColorClose(hex("#0000ff"), interpolator.interpolate(1f))
    }

    @Test
    fun quantize_samplesUniformly() {
        assertEquals(listOf(0, 2, 4, 6, 8), quantize(5, interpolateRound(0f, 8f)))
    }

    @Test
    fun interpolateDiscrete_flooringToIndices() {
        val interpolator = interpolateDiscrete(listOf("a", "b", "c"))
        assertEquals("a", interpolator.interpolate(0f))
        assertEquals("b", interpolator.interpolate(0.4f))
        assertEquals("c", interpolator.interpolate(0.7f))
        assertEquals("c", interpolator.interpolate(1f))
    }

    @Test
    fun interpolateList_elementWise() {
        val interpolator = interpolateList(listOf(0f, 0f), listOf(10f, 20f)) { a, b -> interpolator(a, b) }
        assertEquals(listOf(5f, 10f), interpolator.interpolate(0.5f))
    }
}
