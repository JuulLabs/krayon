package com.juul.krayon.color

import kotlin.random.Random

/** Get a random [Color]. If [isOpaque] is `true` (the default), then alpha is guaranteed to be `0xFF`. */
public fun Random.nextColor(isOpaque: Boolean = true): Color = when (isOpaque) {
    true -> Color((0xFF shl SHIFT_ALPHA) or (nextInt() and MASK_RGB))
    false -> Color(nextInt())
}
