package com.juul.krayon.color

import kotlin.jvm.JvmInline
import kotlin.math.roundToInt

/** A color in ARGB color space. */
@JvmInline
public value class Color(public val argb: Int) {

    /** Create a color from component integers. Components are masked to their last eight bits. */
    public constructor(alpha: Int, red: Int, green: Int, blue: Int) :
        this(
            ((alpha and MASK_COMPONENT) shl SHIFT_ALPHA) or
                ((red and MASK_COMPONENT) shl SHIFT_RED) or
                ((green and MASK_COMPONENT) shl SHIFT_GREEN) or
                ((blue and MASK_COMPONENT) shl SHIFT_BLUE),
        )

    /** Create an opaque color from component integers. Components are masked to their last eight bits. */
    public constructor(red: Int, green: Int, blue: Int) :
        this(alpha = COMPONENT_MAX, red, green, blue)

    /** Create a color from component floats. Components are multiplied by 255 and rounded. */
    public constructor(alpha: Float, red: Float, green: Float, blue: Float) : this(
        alpha.toColorComponent(),
        red.toColorComponent(),
        green.toColorComponent(),
        blue.toColorComponent(),
    )

    /** Create an opaque color from component floats. Components are multiplied by 255 and rounded. */
    public constructor(red: Float, green: Float, blue: Float) :
        this(alpha = FLOAT_COMPONENT_MAX, red, green, blue)

    /** Gets the alpha component of this color as an eight bit integer. */
    public val alpha: Int get() = (argb ushr SHIFT_ALPHA) and MASK_COMPONENT

    /** Gets the red component of this color as an eight bit integer. */
    public val red: Int get() = (argb ushr SHIFT_RED) and MASK_COMPONENT

    /** Gets the green component of this color as an eight bit integer. */
    public val green: Int get() = (argb ushr SHIFT_GREEN) and MASK_COMPONENT

    /** Gets the blue component of this color as an eight bit integer. */
    public val blue: Int get() = (argb ushr SHIFT_BLUE) and MASK_COMPONENT

    /** Gets the rgb component of this color as a 24 bit integer. */
    public val rgb: Int get() = argb and MASK_RGB

    /** Creates a copy of this color. */
    public fun copy(
        alpha: Int = this.alpha,
        red: Int = this.red,
        green: Int = this.green,
        blue: Int = this.blue,
    ): Color = Color(alpha, red, green, blue)

    /** Returns this as a string in web-friendly hex notation. */
    public fun toHexString(): String = buildString {
        append('#')
        append(red.toString(HEX_BASE).padStart(2, '0'))
        append(green.toString(HEX_BASE).padStart(2, '0'))
        append(blue.toString(HEX_BASE).padStart(2, '0'))
        if (alpha != COMPONENT_MAX) {
            append(alpha.toString(HEX_BASE).padStart(2, '0'))
        }
    }

    override fun toString(): String = "Color(${toHexString()})"
}

private fun Float.toColorComponent(): Int {
    require(this in FLOAT_COMPONENT_RANGE) { "Value $this was outside expected range of [0, 1]." }
    return (this * COMPONENT_MAX).roundToInt()
}
