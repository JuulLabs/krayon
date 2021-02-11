package com.juul.krayon.canvas

import kotlin.math.roundToInt
import kotlin.random.Random

private const val COMPONENT_MASK = 0xFF
private const val RGB_MASK = 0xFFFFFF

private const val ALPHA_SHIFT = 24
private const val RED_SHIFT = 16
private const val GREEN_SHIFT = 8
private const val BLUE_SHIFT = 0

/** A color in ARGB color space. */
public inline class Color(public val argb: Int) {

    public constructor(alpha: Int, red: Int, green: Int, blue: Int) :
        this(
            ((alpha and COMPONENT_MASK) shl ALPHA_SHIFT) or
                ((red and COMPONENT_MASK) shl RED_SHIFT) or
                ((green and COMPONENT_MASK) shl GREEN_SHIFT) or
                ((blue and COMPONENT_MASK) shl BLUE_SHIFT)
        )

    public constructor(red: Int, green: Int, blue: Int) :
        this(alpha = 0xFF, red, green, blue)

    public constructor(alpha: Float, red: Float, green: Float, blue: Float) : this(
        (alpha * 0xFF).roundToInt(),
        (red * 0xFF).roundToInt(),
        (green * 0xFF).roundToInt(),
        (blue * 0xFF).roundToInt()
    )

    public constructor(red: Float, green: Float, blue: Float) :
        this(alpha = 1f, red, green, blue)

    public val alpha: Int get() = (argb ushr ALPHA_SHIFT) and COMPONENT_MASK
    public val red: Int get() = (argb ushr RED_SHIFT) and COMPONENT_MASK
    public val green: Int get() = (argb ushr GREEN_SHIFT) and COMPONENT_MASK
    public val blue: Int get() = (argb ushr BLUE_SHIFT) and COMPONENT_MASK

    public fun copy(
        alpha: Int = this.alpha,
        red: Int = this.red,
        green: Int = this.green,
        blue: Int = this.blue,
    ): Color = Color(alpha, red, green, blue)

    /** Linear interpolate towards another color. */
    public fun lerp(other: Color, percent: Float): Color = Color(
        alpha = (this.alpha + (other.alpha - this.alpha) * percent).roundToInt(),
        red = (this.red + (other.red - this.red) * percent).roundToInt(),
        green = (this.green + (other.green - this.green) * percent).roundToInt(),
        blue = (this.blue + (other.blue - this.blue) * percent).roundToInt()
    )

    public companion object {
        public val transparent: Color = Color(0x00, 0x00, 0x00, 0x00)

        public val black: Color = Color(0x00, 0x00, 0x00)
        public val white: Color = Color(0xFF, 0xFF, 0xFF)

        public val red: Color = Color(0xFF, 0x00, 0x00)
        public val green: Color = Color(0x00, 0xFF, 0x00)
        public val blue: Color = Color(0x00, 0x00, 0xFF)

        public val cyan: Color = Color(0x00, 0xFF, 0xFF)
        public val magenta: Color = Color(0xFF, 0x00, 0xFF)
        public val yellow: Color = Color(0xFF, 0xFF, 0x00)
    }
}

/** Get a random [Color]. If [isOpaque] is `true` (the default), then alpha is guaranteed to be `0xFF`. */
public fun Random.nextColor(isOpaque: Boolean = true): Color = when (isOpaque) {
    true -> Color((0xFF shl ALPHA_SHIFT) or (nextInt() and RGB_MASK))
    false -> Color(nextInt())
}
