package com.juul.krayon.interpolate

import com.juul.krayon.color.Color
import com.juul.krayon.color.schemeBlues
import com.juul.krayon.color.schemeGreens
import com.juul.krayon.color.schemeGreys
import com.juul.krayon.color.schemeRdBu
import com.juul.krayon.color.schemeReds
import com.juul.krayon.color.schemeSpectral
import com.juul.krayon.color.schemeViridis

/** Continuous Blues sequential color scheme (single-hue). */
public val interpolateBlues: Interpolator<Color> = interpolateRgbBasis(schemeBlues)

/** Continuous Greens sequential color scheme (single-hue). */
public val interpolateGreens: Interpolator<Color> = interpolateRgbBasis(schemeGreens)

/** Continuous Reds sequential color scheme (single-hue). */
public val interpolateReds: Interpolator<Color> = interpolateRgbBasis(schemeReds)

/** Continuous Greys sequential color scheme (single-hue). */
public val interpolateGreys: Interpolator<Color> = interpolateRgbBasis(schemeGreys)

/**
 * Continuous Viridis sequential color scheme (multi-hue). Like d3, the 256-swatch palette is
 * sampled by nearest swatch rather than spline-interpolated.
 */
public val interpolateViridis: Interpolator<Color> = interpolateDiscrete(schemeViridis)

/** Continuous RdBu diverging color scheme. */
public val interpolateRdBu: Interpolator<Color> = interpolateRgbBasis(schemeRdBu)

/** Continuous Spectral diverging color scheme. */
public val interpolateSpectral: Interpolator<Color> = interpolateRgbBasis(schemeSpectral)
