package com.juul.krayon.canvas

/** Describes an area to be included or excluded in future draw calls. */
public sealed class Clip<out PATH> {

    public abstract val operation: Clip.Operation

    /** Use a rectangle based clip. */
    public data class Rect<PATH>(
        public val left: Float,
        public val top: Float,
        public val right: Float,
        public val bottom: Float,
        public override val operation: Operation = Operation.Intersection,
    ) : Clip<PATH>() {

        /** Returns a copy of this rectangle with a new generic type. */
        public fun <T> cast(): Rect<T> = Rect(left, top, right, bottom, operation)
    }

    /** Use a path based clip. It's important that the [path] is of the type [P] returned by the [Canvas.buildPath]. */
    public data class Path<PATH>(
        public val path: PATH,
        public override val operation: Operation = Operation.Intersection,
    ) : Clip<PATH>()

    /** Specifies path inclusion behavior. */
    public enum class Operation {

        /** Limit future drawing to areas inside the path. */
        Intersection,

        /** Limit future drawing to areas outside the path. */
        Difference
    }
}
