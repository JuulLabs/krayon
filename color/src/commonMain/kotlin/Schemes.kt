package com.juul.krayon.color

/**
 * Categorical, sequential, and diverging color schemes ported from
 * [d3-scale-chromatic](https://github.com/d3/d3-scale-chromatic).
 *
 * Categorical `scheme*` values are ordered palettes for discrete data. Sequential and diverging
 * `scheme*` values are the control points consumed by the matching `interpolate*` functions in the
 * `interpolate` module.
 */
private fun colors(specifier: String): List<Color> =
    List(specifier.length / 6) { i ->
        val rgb = specifier.substring(i * 6, i * 6 + 6).toInt(HEX_BASE)
        Color((0xFF shl SHIFT_ALPHA) or rgb)
    }

public val schemeCategory10: List<Color> =
    colors("1f77b4ff7f0e2ca02cd627289467bd8c564be377c27f7f7fbcbd2217becf")

public val schemeTableau10: List<Color> =
    colors("4e79a7f28e2ce1575976b7b259a14fedc949af7aa1ff9da79c755fbab0ab")

public val schemeSet1: List<Color> =
    colors("e41a1c377eb84daf4a984ea3ff7f00ffff33a65628f781bf999999")

public val schemeSet2: List<Color> =
    colors("66c2a5fc8d628da0cbe78ac3a6d854ffd92fe5c494b3b3b3")

public val schemeSet3: List<Color> =
    colors("8dd3c7ffffb3bebadafb807280b1d3fdb462b3de69fccde5d9d9d9bc80bdccebc5ffed6f")

public val schemeBlues: List<Color> =
    colors("f7fbffdeebf7c6dbef9ecae16baed64292c62171b508519c08306b")

public val schemeGreens: List<Color> =
    colors("f7fcf5e5f5e0c7e9c0a1d99b74c47641ab5d238b45006d2c00441b")

public val schemeReds: List<Color> =
    colors("fff5f0fee0d2fcbba1fc9272fb6a4aef3b2ccb181da50f1567000d")

public val schemeGreys: List<Color> =
    colors("fffffff0f0f0d9d9d9bdbdbd969696737373525252252525000000")

public val schemeRdBu: List<Color> =
    colors("67001fb2182bd6604df4a582fddbc7f7f7f7d1e5f092c5de4393c32166ac053061")

public val schemeSpectral: List<Color> =
    colors("9e0142d53e4ff46d43fdae61fee08bffffbfe6f598abdda466c2a53288bd5e4fa2")

public val schemeViridis: List<Color> =
    colors(VIRIDIS_SPECIFIER)
