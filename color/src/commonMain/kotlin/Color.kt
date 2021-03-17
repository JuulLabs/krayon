package com.juul.krayon.color

import kotlin.math.roundToInt
import kotlin.random.Random

private const val COMPONENT_MASK = 0xFF
private const val RGB_MASK = 0xFFFFFF

private const val ALPHA_SHIFT = 24
private const val RED_SHIFT = 16
private const val GREEN_SHIFT = 8
private const val BLUE_SHIFT = 0

private val FLOAT_COMPONENT_RANGE = 0f..1f

private fun requireInRange(float: Float): Float {
    require(float in FLOAT_COMPONENT_RANGE)
    return float
}

/** A color in ARGB color space. */
public inline class Color(public val argb: Int) {

    /** Create a color from component integers. Components are masked to their last eight bits. */
    public constructor(alpha: Int, red: Int, green: Int, blue: Int) :
        this(
            ((alpha and COMPONENT_MASK) shl ALPHA_SHIFT) or
                ((red and COMPONENT_MASK) shl RED_SHIFT) or
                ((green and COMPONENT_MASK) shl GREEN_SHIFT) or
                ((blue and COMPONENT_MASK) shl BLUE_SHIFT)
        )

    /** Create an opaque color from component integers. Components are masked to their last eight bits. */
    public constructor(red: Int, green: Int, blue: Int) :
        this(alpha = 0xFF, red, green, blue)

    /** Create a color from component floats. Components are multiplied by 255 and rounded. */
    public constructor(alpha: Float, red: Float, green: Float, blue: Float) : this(
        (requireInRange(alpha) * 0xFF).roundToInt(),
        (requireInRange(red) * 0xFF).roundToInt(),
        (requireInRange(green) * 0xFF).roundToInt(),
        (requireInRange(blue) * 0xFF).roundToInt()
    )

    /** Create an opaque color from component floats. Components are multiplied by 255 and rounded. */
    public constructor(red: Float, green: Float, blue: Float) :
        this(alpha = 1f, red, green, blue)

    /** Gets the alpha component of this color as an eight bit integer. */
    public val alpha: Int get() = (argb ushr ALPHA_SHIFT) and COMPONENT_MASK

    /** Gets the red component of this color as an eight bit integer. */
    public val red: Int get() = (argb ushr RED_SHIFT) and COMPONENT_MASK

    /** Gets the green component of this color as an eight bit integer. */
    public val green: Int get() = (argb ushr GREEN_SHIFT) and COMPONENT_MASK

    /** Gets the blue component of this color as an eight bit integer. */
    public val blue: Int get() = (argb ushr BLUE_SHIFT) and COMPONENT_MASK

    /** Gets the rgb component of this color as a 24 bit integer. */
    public val rgb: Int get() = argb and RGB_MASK

    /** Creates a copy of this color. */
    public fun copy(
        alpha: Int = this.alpha,
        red: Int = this.red,
        green: Int = this.green,
        blue: Int = this.blue,
    ): Color = Color(alpha, red, green, blue)

    override fun toString(): String = "Color(${argb.toString(16).padStart(8, '0')})"

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

/** Linear interpolate towards another color. */
public fun Color.lerp(other: Color, percent: Float): Color = Color(
    alpha = (this.alpha + (other.alpha - this.alpha) * percent).roundToInt(),
    red = (this.red + (other.red - this.red) * percent).roundToInt(),
    green = (this.green + (other.green - this.green) * percent).roundToInt(),
    blue = (this.blue + (other.blue - this.blue) * percent).roundToInt()
)

/** Get a random [Color]. If [isOpaque] is `true` (the default), then alpha is guaranteed to be `0xFF`. */
public fun Random.nextColor(isOpaque: Boolean = true): Color = when (isOpaque) {
    true -> Color((0xFF shl ALPHA_SHIFT) or (nextInt() and RGB_MASK))
    false -> Color(nextInt())
}
