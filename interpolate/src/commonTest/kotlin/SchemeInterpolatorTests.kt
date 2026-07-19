package com.juul.krayon.interpolate

import kotlin.test.Test

class SchemeInterpolatorTests {

    @Test
    fun interpolateBlues_matchesD3() {
        assertColorClose(hex("#f7fbff"), interpolateBlues.interpolate(0f))
        assertColorClose(hex("#6daed5"), interpolateBlues.interpolate(0.5f))
        assertColorClose(hex("#08306b"), interpolateBlues.interpolate(1f))
    }

    @Test
    fun interpolateViridis_matchesD3() {
        assertColorClose(hex("#440154"), interpolateViridis.interpolate(0f))
        assertColorClose(hex("#21918c"), interpolateViridis.interpolate(0.5f))
        assertColorClose(hex("#fde725"), interpolateViridis.interpolate(1f))
    }

    @Test
    fun interpolateRdBu_matchesD3() {
        assertColorClose(hex("#67001f"), interpolateRdBu.interpolate(0f))
        assertColorClose(hex("#f2efee"), interpolateRdBu.interpolate(0.5f))
        assertColorClose(hex("#053061"), interpolateRdBu.interpolate(1f))
    }

    @Test
    fun interpolateSpectral_matchesD3() {
        assertColorClose(hex("#9e0142"), interpolateSpectral.interpolate(0f))
        assertColorClose(hex("#fbf8b0"), interpolateSpectral.interpolate(0.5f))
        assertColorClose(hex("#5e4fa2"), interpolateSpectral.interpolate(1f))
    }
}
