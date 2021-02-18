package com.juul.krayon.kanvas

/** Describes an area to be included or excluded in future draw calls. */
public sealed class Clip<out PATH> {

    /** Use a rectangle based clip. */
    public data class Rect<PATH>(
        public val left: Float,
        public val top: Float,
        public val right: Float,
        public val bottom: Float
    ) : Clip<PATH>() {

        /** Returns a copy of this rectangle with a new generic type. */
        public fun <T> cast(): Rect<T> = Rect(left, top, right, bottom)
    }

    /** Use a path based clip. It's important that the [path] is of the type [P] returned by the [Canvas.buildPath]. */
    public data class Path<PATH>(
        public val path: PATH
    ) : Clip<PATH>()
}
