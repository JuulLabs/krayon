package com.juul.krayon.color

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColorSpaceTests {

    private val tol = 1e-2f

    @Test
    fun toHsl_red_matchesD3() {
        val hsl = red.toHsl()
        assertEquals(0f, hsl.h, tol)
        assertEquals(1f, hsl.s, tol)
        assertEquals(0.5f, hsl.l, tol)
    }

    @Test
    fun toHsl_cornflowerBlue_matchesD3() {
        val hsl = cornflowerBlue.toHsl()
        assertEquals(218.5401f, hsl.h, tol)
        assertEquals(0.7919f, hsl.s, tol)
        assertEquals(0.6608f, hsl.l, tol)
    }

    @Test
    fun toHsl_white_hasNaNHueAndSaturation() {
        val hsl = white.toHsl()
        assertTrue(hsl.h.isNaN())
        assertTrue(hsl.s.isNaN())
        assertEquals(1f, hsl.l, tol)
    }

    @Test
    fun toLab_red_matchesD3() {
        val lab = red.toLab()
        assertEquals(54.2917f, lab.l, tol)
        assertEquals(80.8125f, lab.a, tol)
        assertEquals(69.8850f, lab.b, tol)
    }

    @Test
    fun toLab_darkBlueGray_matchesD3() {
        val lab = "#336699".toColor().toLab()
        assertEquals(41.5206f, lab.l, tol)
        assertEquals(-4.5733f, lab.a, tol)
        assertEquals(-33.4939f, lab.b, tol)
    }

    @Test
    fun toHcl_red_matchesD3() {
        val hcl = red.toHcl()
        assertEquals(40.8526f, hcl.h, tol)
        assertEquals(106.8390f, hcl.c, tol)
        assertEquals(54.2917f, hcl.l, tol)
    }

    @Test
    fun toCubehelix_red_matchesD3() {
        val cubehelix = red.toCubehelix()
        assertEquals(351.8103f, cubehelix.h, tol)
        assertEquals(1.9489f, cubehelix.s, tol)
        assertEquals(0.3000f, cubehelix.l, tol)
    }

    @Test
    fun hslRoundTrip_isStable() {
        for (color in roundTripSamples) {
            assertColorClose(color, color.toHsl().toColor())
        }
    }

    @Test
    fun labRoundTrip_isStable() {
        for (color in roundTripSamples) {
            assertColorClose(color, color.toLab().toColor())
        }
    }

    @Test
    fun hclRoundTrip_isStable() {
        for (color in roundTripSamples) {
            assertColorClose(color, color.toHcl().toColor())
        }
    }

    @Test
    fun cubehelixRoundTrip_isStable() {
        for (color in roundTripSamples) {
            assertColorClose(color, color.toCubehelix().toColor())
        }
    }

    @Test
    fun lch_reordersToHcl() {
        assertEquals(Hcl(40f, 30f, 50f), lch(l = 50f, c = 30f, h = 40f))
    }

    private val roundTripSamples = listOf(
        red,
        lime,
        blue,
        white,
        black,
        cornflowerBlue,
        steelBlue,
        orange,
        "#336699".toColor(),
    )
}
