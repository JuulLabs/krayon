package com.juul.krayon.kanvas

/** Default chosen to match SVG. See https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-miterlimit */
public const val DEFAULT_MITER_LIMIT: Float = 4f

/** A descriptor for how shapes should be painted. */
public sealed class Paint {

    /** Fills the inside of shape with a color. */
    public data class Fill(
        public val color: Color,
    ) : Paint()

    /** Draws a color around the outside of a shape. */
    public data class Stroke(
        public val color: Color,
        public val width: Float,
        public val cap: Cap = Cap.Butt,
        public val join: Join = Join.Miter(),
    ) : Paint() {

        /** Shape behavior for end of a line. */
        public enum class Cap {
            /** Squared off end, starting or stopping at exactly the edge of the line. */
            Butt,

            /** Rounded end, extending past the edge of the line by half the stroke width. */
            Round,

            /** Squared off end, extending past the edge of the line by half the stroke width. */
            Square,
        }

        /** Shape behavior for the corners in a poly-line. */
        public sealed class Join {
            /** Corners should make nice pointy points. */
            public data class Miter(
                /**
                 * The maximum ratio of miter length to stroke width to render miters.
                 * If the ratio exceeds this limit, then a [Bevel] is rendered instead.
                 *
                 * Defaults to 4, which uses a miter at 90 degrees or more and a bevel below 90 degrees.
                 */
                public val limit: Float = DEFAULT_MITER_LIMIT
            ) : Join()

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
        public val font: Font,
    ) : Paint() {
        public enum class Alignment { Left, Center, Right }
    }
}
