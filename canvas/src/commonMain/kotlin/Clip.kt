package com.juul.krayon.canvas

import com.juul.krayon.canvas.Path as PathMarker

/** Describes an area to be included or excluded in future draw calls. */
public sealed class Clip {

    /** Use a rectangle based clip. */
    public data class Rect(
        public val left: Float,
        public val top: Float,
        public val right: Float,
        public val bottom: Float,
        public val operation: Operation = Operation.Intersection
    ) : Clip()

    /** Use a path based clip. It's important that the [path] is of the type [P] returned by the [Canvas.buildPath]. */
    public data class Path<P : PathMarker>(
        public val path: P,
        public val operation: Operation = Operation.Intersection
    ) : Clip()

    /** Specifies path inclusion behavior. */
    public enum class Operation {

        /** Limit future drawing to areas inside the path. */
        Intersection,

        /** Limit future drawing to areas outside the path. */
        Difference
    }
}
