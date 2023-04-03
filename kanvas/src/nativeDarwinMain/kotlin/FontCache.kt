package com.juul.krayon.kanvas

import platform.CoreText.CTFontCreateWithName
import platform.CoreText.CTFontRef

/** There's a fine line between aggressive caching and a memory leak. */
internal object FontCache {

    private val cache = mutableMapOf<Pair<String, Float>, CTFontRef?>()

    operator fun get(name: String, size: Float) = cache.getOrPut(name to size) {
        withCFString(name) { cfString ->
            CTFontCreateWithName(cfString, size.toDouble(), null)
        }
    }
}
