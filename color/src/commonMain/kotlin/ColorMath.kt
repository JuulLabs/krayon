package com.juul.krayon.color

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt

internal const val DEGREES: Float = 180f / PI.toFloat()
internal const val RADIANS: Float = PI.toFloat() / 180f

/** The channel scale factor d3-color uses for one step of [Color.darker]. */
internal const val DARKER: Float = 0.7f

/** Converts an sRGB channel in `[0, 255]` to linear-light RGB in `[0, 1]`. */
internal fun srgbToLinear(channel: Float): Float {
    val normalized = channel / 255f
    return if (normalized <= 0.04045f) normalized / 12.92f else ((normalized + 0.055f) / 1.055f).pow(2.4f)
}

/** Converts a linear-light RGB channel in `[0, 1]` to an sRGB channel in `[0, 255]`. */
internal fun linearToSrgb(channel: Float): Float =
    255f * (if (channel <= 0.0031308f) 12.92f * channel else 1.055f * channel.pow(1f / 2.4f) - 0.055f)

internal fun Color.redFraction(): Float = red / 255f

internal fun Color.greenFraction(): Float = green / 255f

internal fun Color.blueFraction(): Float = blue / 255f

internal fun Color.opacityFraction(): Float = alpha / 255f

internal fun opacityToAlpha(opacity: Float): Int = (opacity.coerceIn(0f, 1f) * 255f).roundToInt()

/** Rounds a possibly out-of-range channel to a valid `[0, 255]` integer component. */
internal fun channelToInt(value: Float): Int = value.roundToInt().coerceIn(COMPONENT_MIN, COMPONENT_MAX)
