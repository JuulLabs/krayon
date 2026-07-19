package com.juul.krayon.interpolate

import com.juul.krayon.color.Color
import com.juul.krayon.color.Cubehelix
import com.juul.krayon.color.Hcl
import com.juul.krayon.color.Hsl
import com.juul.krayon.color.Lab
import com.juul.krayon.color.toColor
import com.juul.krayon.color.toCubehelix
import com.juul.krayon.color.toHcl
import com.juul.krayon.color.toHsl
import com.juul.krayon.color.toLab
import kotlin.math.pow

private fun Color.opacity(): Float = alpha / 255f

/**
 * Interpolates between two colors in the sRGB color space. A [gamma] of `1` (the default) is a
 * simple linear interpolation; other values apply gamma correction as in d3-interpolate.
 */
public fun interpolateRgb(start: Color, stop: Color, gamma: Float = 1f): Interpolator<Color> {
    val r = gammaChannel(start.red.toFloat(), stop.red.toFloat(), gamma)
    val g = gammaChannel(start.green.toFloat(), stop.green.toFloat(), gamma)
    val b = gammaChannel(start.blue.toFloat(), stop.blue.toFloat(), gamma)
    val o = interpolateChannel(start.opacity(), stop.opacity())
    return FunctionInterpolator { t -> buildColor(r(t), g(t), b(t), o(t)) }
}

/** Interpolates between two colors in HSL, taking the shortest path around the hue circle. */
public fun interpolateHsl(start: Color, stop: Color): Interpolator<Color> =
    hslInterpolator(start.toHsl(), stop.toHsl(), ::interpolateHue)

/** Interpolates between two colors in HSL, allowing hue to travel more than 180 degrees. */
public fun interpolateHslLong(start: Color, stop: Color): Interpolator<Color> =
    hslInterpolator(start.toHsl(), stop.toHsl(), ::interpolateChannel)

private fun hslInterpolator(start: Hsl, stop: Hsl, hue: (Float, Float) -> (Float) -> Float): Interpolator<Color> {
    val h = hue(start.hue, stop.hue)
    val s = interpolateChannel(start.saturation, stop.saturation)
    val l = interpolateChannel(start.lightness, stop.lightness)
    val o = interpolateChannel(start.opacity, stop.opacity)
    return FunctionInterpolator { t -> Hsl(h(t), s(t), l(t), o(t)).toColor() }
}

/** Interpolates between two colors in the CIELAB color space. */
public fun interpolateLab(start: Color, stop: Color): Interpolator<Color> {
    val startLab = start.toLab()
    val stopLab = stop.toLab()
    val l = interpolateChannel(startLab.lightness, stopLab.lightness)
    val a = interpolateChannel(startLab.a, stopLab.a)
    val b = interpolateChannel(startLab.b, stopLab.b)
    val o = interpolateChannel(startLab.opacity, stopLab.opacity)
    return FunctionInterpolator { t -> Lab(l(t), a(t), b(t), o(t)).toColor() }
}

/** Interpolates between two colors in HCL, taking the shortest path around the hue circle. */
public fun interpolateHcl(start: Color, stop: Color): Interpolator<Color> =
    hclInterpolator(start.toHcl(), stop.toHcl(), ::interpolateHue)

/** Interpolates between two colors in HCL, allowing hue to travel more than 180 degrees. */
public fun interpolateHclLong(start: Color, stop: Color): Interpolator<Color> =
    hclInterpolator(start.toHcl(), stop.toHcl(), ::interpolateChannel)

private fun hclInterpolator(start: Hcl, stop: Hcl, hue: (Float, Float) -> (Float) -> Float): Interpolator<Color> {
    val h = hue(start.hue, stop.hue)
    val c = interpolateChannel(start.chroma, stop.chroma)
    val l = interpolateChannel(start.lightness, stop.lightness)
    val o = interpolateChannel(start.opacity, stop.opacity)
    return FunctionInterpolator { t -> Hcl(h(t), c(t), l(t), o(t)).toColor() }
}

/** Interpolates between two colors in Cubehelix, taking the shortest path around the hue circle. */
public fun interpolateCubehelix(start: Color, stop: Color, gamma: Float = 1f): Interpolator<Color> =
    cubehelixInterpolator(start.toCubehelix(), stop.toCubehelix(), gamma, ::interpolateHue)

/** Interpolates between two colors in Cubehelix, allowing hue to travel more than 180 degrees. */
public fun interpolateCubehelixLong(start: Color, stop: Color, gamma: Float = 1f): Interpolator<Color> =
    cubehelixInterpolator(start.toCubehelix(), stop.toCubehelix(), gamma, ::interpolateChannel)

private fun cubehelixInterpolator(
    start: Cubehelix,
    stop: Cubehelix,
    gamma: Float,
    hue: (Float, Float) -> (Float) -> Float,
): Interpolator<Color> {
    val h = hue(start.hue, stop.hue)
    val s = interpolateChannel(start.saturation, stop.saturation)
    val l = interpolateChannel(start.lightness, stop.lightness)
    val o = interpolateChannel(start.opacity, stop.opacity)
    return FunctionInterpolator { t -> Cubehelix(h(t), s(t), l(t.pow(gamma)), o(t)).toColor() }
}
