package com.juul.krayon.color

import kotlin.math.roundToInt

/** Linear interpolate towards another color. */
public fun lerp(from: Color, to: Color, percent: Float): Color = Color(
    alpha = (from.alpha + (to.alpha - from.alpha) * percent).roundToInt(),
    red = (from.red + (to.red - from.red) * percent).roundToInt(),
    green = (from.green + (to.green - from.green) * percent).roundToInt(),
    blue = (from.blue + (to.blue - from.blue) * percent).roundToInt()
)
