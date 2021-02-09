package com.juul.krayon.canvas

private const val COMPONENT_MASK = 0xFF

private const val ALPHA_SHIFT = 24
private const val RED_SHIFT = 16
private const val GREEN_SHIFT = 8
private const val BLUE_SHIFT = 0

/** A color in ARGB color space. */
public inline class Color(public val argb: Int) {

    public constructor(alpha: Int, red: Int, green: Int, blue: Int) :
        this((alpha shl ALPHA_SHIFT) or (red shl RED_SHIFT) or (green shl GREEN_SHIFT) or (blue shl BLUE_SHIFT))

    public constructor(red: Int, green: Int, blue: Int) :
        this(alpha = 0xFF, red, green, blue)

    public val alpha: Int get() = (argb ushr ALPHA_SHIFT) and COMPONENT_MASK
    public val red: Int get() = (argb ushr RED_SHIFT) and COMPONENT_MASK
    public val green: Int get() = (argb ushr GREEN_SHIFT) and COMPONENT_MASK
    public val blue: Int get() = (argb ushr BLUE_SHIFT) and COMPONENT_MASK

    public fun copy(
        alpha: Int = this.alpha,
        red: Int = this.red,
        green: Int = this.green,
        blue: Int = this.blue
    ): Color = Color(alpha, red, green, blue)

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
