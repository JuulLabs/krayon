package com.juul.krayon.kanvas

import com.juul.krayon.color.Color

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
        public val dash: Dash = Dash.None,
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
                public val limit: Float = DEFAULT_MITER_LIMIT,
            ) : Join()

            /** Corners should be rounded off. */
            public object Round : Join()

            /** Corners should be squared off. */
            public object Bevel : Join()
        }

        public sealed class Dash {
            public object None : Dash()
            public data class Pattern(val intervals: List<Float>) : Dash() {
                public constructor(vararg intervals: Float) : this(intervals.toList())

                init {
                    check(intervals.isNotEmpty()) { "Cannot construct pattern without intervals. Use Dash.None instead." }
                    check(intervals.size % 2 == 0) { "Patterns must contain an even number of items. An interval is created for each even/odd index pair." }
                }
            }
        }
    }

    public data class FillAndStroke(
        public val fill: Fill,
        public val stroke: Stroke,
    ) : Paint()

    public sealed class Gradient : Paint() {

        public abstract val stops: List<Stop>

        init {
            @Suppress("LeakingThis")
            require(stops.sortedBy { it.offset } == stops) { "Gradient stops must be ascending by offset, but were $stops." }
        }

        public data class Linear(
            val startX: Float,
            val startY: Float,
            val endX: Float,
            val endY: Float,
            override val stops: List<Stop>,
        ) : Gradient() {
            public constructor(startX: Float, startY: Float, endX: Float, endY: Float, vararg stops: Stop) :
                this(startX, startY, endX, endY, stops.toList())
        }

        public data class Radial(
            val startX: Float,
            val startY: Float,
            val startRadius: Float,
            val endX: Float,
            val endY: Float,
            val endRadius: Float,
            override val stops: List<Stop>,
        ) : Gradient() {
            public constructor(startX: Float, startY: Float, startRadius: Float, endX: Float, endY: Float, endRadius: Float, vararg stops: Stop) :
                this(startX, startY, startRadius, endX, endY, endRadius, stops.toList())

            public constructor(centerX: Float, centerY: Float, endRadius: Float, stops: List<Stop>) :
                this(centerX, centerY, startRadius = 0f, centerX, centerY, endRadius, stops)

            public constructor(centerX: Float, centerY: Float, endRadius: Float, vararg stops: Stop) :
                this(centerX, centerY, startRadius = 0f, centerX, centerY, endRadius, stops.toList())
        }

        public data class Sweep(
            val centerX: Float,
            val centerY: Float,
            override val stops: List<Stop>,
        ) : Gradient() {
            public constructor(centerX: Float, centerY: Float, vararg stops: Stop) :
                this(centerX, centerY, stops.toList())
        }

        public data class Stop(
            val offset: Float,
            val color: Color,
        )
    }

    public data class GradientAndStroke(
        public val gradient: Gradient,
        public val stroke: Stroke,
    ) : Paint()

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
