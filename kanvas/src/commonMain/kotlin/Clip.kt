package com.juul.krayon.kanvas

import com.juul.krayon.kanvas.Path as KrayonPath

/** Describes an area to be included or excluded in future draw calls. */
public sealed class Clip {

    /** Use a rectangle based clip. */
    public data class Rect(
        public val left: Float,
        public val top: Float,
        public val right: Float,
        public val bottom: Float,
    ) : Clip()

    /** Use a path based clip. */
    public data class Path(
        public val path: KrayonPath,
    ) : Clip()
}
