package com.juul.krayon.kanvas

/** Represents a transformation. Usually, these will boil down to matrix operations. */
public sealed class Transform {

    /** A series of [transformations]. Remember that transformations are not commutative. */
    public data class InOrder(
        public val transformations: List<Transform>,
    ) : Transform() {
        public constructor(vararg transformations: Transform) : this(transformations.toList())
    }

    /**
     * Scale by [horizontal] and [vertical] factors. If [pivotX] or [pivotY] is
     * specified as non-zero, then the scale will be centered on that location.
     */
    public data class Scale(
        public val horizontal: Float = 1f,
        public val vertical: Float = 1f,
        public val pivotX: Float = 0f,
        public val pivotY: Float = 0f,
    ) : Transform()

    /**
     * Rotate by [degrees]. If [pivotX] or [pivotY] is specified as
     * non-zero, then the rotation will be centered on that location.
     */
    public data class Rotate(
        public val degrees: Float,
        public val pivotX: Float = 0f,
        public val pivotY: Float = 0f,
    ) : Transform()

    /** Translate by [horizontal] and [vertical]. */
    public data class Translate(
        public val horizontal: Float = 0f,
        public val vertical: Float = 0f,
    ) : Transform()

    /** Skew the image by [horizontal] and [vertical]. */
    public data class Skew(
        public val horizontal: Float = 0f,
        public val vertical: Float = 0f,
    ) : Transform()
}

/** Converts a pivoted scale into an ordered transformation of translate, scale, translate. */
public fun Transform.Scale.split(): Transform.InOrder = Transform.InOrder(
    Transform.Translate(pivotX, pivotY),
    Transform.Scale(horizontal, vertical),
    Transform.Translate(-pivotX, -pivotY),
)

/** Converts a bi-direction skew into an ordered transformation of horizontal skew and vertical skew. */
public fun Transform.Skew.split(): Transform.InOrder = Transform.InOrder(
    Transform.Skew(horizontal = horizontal),
    Transform.Skew(vertical = vertical),
)
