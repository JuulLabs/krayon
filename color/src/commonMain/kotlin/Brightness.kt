package com.juul.krayon.color

import kotlin.math.pow

/** Returns a brighter copy of this color by scaling each RGB channel, as in d3-color. */
public fun Color.brighter(k: Float = 1f): Color {
    val factor = (1f / DARKER).pow(k)
    return Color(
        alpha = alpha,
        red = channelToInt(red * factor),
        green = channelToInt(green * factor),
        blue = channelToInt(blue * factor),
    )
}

/** Returns a darker copy of this color by scaling each RGB channel, as in d3-color. */
public fun Color.darker(k: Float = 1f): Color {
    val factor = DARKER.pow(k)
    return Color(
        alpha = alpha,
        red = channelToInt(red * factor),
        green = channelToInt(green * factor),
        blue = channelToInt(blue * factor),
    )
}
