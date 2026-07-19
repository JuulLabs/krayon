package com.juul.krayon.interpolate

import kotlin.test.Test

class ColorInterpolatorTests {

    @Test
    fun interpolateRgb_blackToWhite_matchesD3() {
        val interpolator = interpolateRgb(hex("#000000"), hex("#ffffff"))
        assertColorClose(hex("#000000"), interpolator.interpolate(0f))
        assertColorClose(hex("#808080"), interpolator.interpolate(0.5f))
        assertColorClose(hex("#ffffff"), interpolator.interpolate(1f))
    }

    @Test
    fun interpolateRgb_withGamma_matchesD3() {
        val interpolator = interpolateRgb(hex("#000000"), hex("#ffffff"), gamma = 2.2f)
        assertColorClose(hex("#bababa"), interpolator.interpolate(0.5f))
    }

    @Test
    fun interpolateHsl_redToLime_passesThroughYellow() {
        val interpolator = interpolateHsl(hex("#ff0000"), hex("#00ff00"))
        assertColorClose(hex("#ff0000"), interpolator.interpolate(0f))
        assertColorClose(hex("#ffff00"), interpolator.interpolate(0.5f))
        assertColorClose(hex("#00ff00"), interpolator.interpolate(1f))
    }

    @Test
    fun interpolateLab_blackToWhite_matchesD3() {
        val interpolator = interpolateLab(hex("#000000"), hex("#ffffff"))
        assertColorClose(hex("#777777"), interpolator.interpolate(0.5f))
    }

    @Test
    fun interpolateLab_steelBlueToOrange_matchesD3() {
        val interpolator = interpolateLab(hex("#4682b4"), hex("#ffa500"))
        assertColorClose(hex("#4682b4"), interpolator.interpolate(0f))
        assertColorClose(hex("#b69472"), interpolator.interpolate(0.5f))
        assertColorClose(hex("#ffa500"), interpolator.interpolate(1f))
    }

    @Test
    fun interpolateHcl_steelBlueToOrange_matchesD3() {
        val interpolator = interpolateHcl(hex("#4682b4"), hex("#ffa500"))
        assertColorClose(hex("#ec6dbb"), interpolator.interpolate(0.5f))
    }

    @Test
    fun interpolateCubehelix_blackToWhite_matchesD3() {
        val interpolator = interpolateCubehelix(hex("#000000"), hex("#ffffff"))
        assertColorClose(hex("#808080"), interpolator.interpolate(0.5f))
    }

    @Test
    fun interpolateCubehelixLong_purpleToOrange_matchesD3() {
        val interpolator = interpolateCubehelixLong(hex("#800080"), hex("#ffa500"))
        assertColorClose(hex("#800080"), interpolator.interpolate(0f))
        assertColorClose(hex("#00c29e"), interpolator.interpolate(0.5f))
        assertColorClose(hex("#ffa500"), interpolator.interpolate(1f))
    }
}
