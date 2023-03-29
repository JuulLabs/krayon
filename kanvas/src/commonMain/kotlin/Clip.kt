package com.juul.krayon.kanvas

import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.Path as KrayonPath

/** Describes an area to be included or excluded in future draw calls. */
public sealed class Clip {
    public abstract val path: KrayonPath

    /** Use a rectangle based clip. */
    public data class Rect(
        public val left: Float,
        public val top: Float,
        public val right: Float,
        public val bottom: Float,
    ) : Clip() {
        override val path: KrayonPath by lazy {
            KrayonPath {
                moveTo(left, top)
                lineTo(right, top)
                lineTo(right, bottom)
                lineTo(left, bottom)
                close()
            }
        }
    }

    /** Use a path based clip. */
    public data class Path(
        public override val path: KrayonPath,
    ) : Clip()
}
