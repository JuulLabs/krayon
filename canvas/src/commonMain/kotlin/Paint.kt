package com.juul.krayon.canvas

/** A descriptor for how shapes should be painted. */
public sealed class Paint {

    /** Fills the inside of shape with a color. */
    public data class Fill(
        public val color: Color
    ) : Paint()

    /** Draws a color around the outside of a shape. */
    public data class Stroke(
        public val color: Color,
        public val width: Float,
        public val cap: Cap = Cap.Butt,
        public val join: Join = Join.Miter()
    ) : Paint() {

        /** Shape behavior for end of a line. */
        public enum class Cap {
            /** Squared off end, starting or stopping at exactly the edge of the line. */
            Butt,

            /** Rounded end, extending past the edge of the line by half the stroke width. */
            Round,

            /** Squared off end, extending past the edge of the line by half the stroke width. */
            Square
        }

        /** Shape behavior for the corners in a poly-line. */
        public sealed class Join {
            /** Corners should make nice pointy points. */
            public data class Miter(public val limit: Float = 0f) : Join()

            /** Corners should be rounded off. */
            public object Round : Join()

            /** Corners should be squared off. */
            public object Bevel : Join()
        }
    }

    /** Similar to [Fill], but carrying additional information necessary for text. */
    public data class Text(
        public val color: Color,
        public val size: Float,
        public val alignment: Alignment,
        public val font: Font
    ) : Paint() {
        public enum class Alignment { Left, Center, Right }
    }
}
