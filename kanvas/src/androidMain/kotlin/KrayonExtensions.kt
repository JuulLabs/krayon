package com.juul.krayon.kanvas

import android.content.res.Resources
import androidx.annotation.FontRes
import com.juul.krayon.core.Krayon
import com.juul.krayon.core.cache.InfiniteCache

/** Cache from [Font.name] to [FontRes] id. */
internal val fontResources = InfiniteCache<String, Int>()

/**
 * Associate a [Font.name] to a [FontRes] id. As a best-practice, do this up front for any
 * fonts that might be used, as calls to [addFontAssociation] are cheap and the behavior
 * for missing associations is expensive ([Resources.getIdentifier]).
 *
 * These associations are only used when drawing to an [AndroidKanvas]. If using compose with a
 * [ComposeKanvas], see the overload with [FontResource] instead.
 */
@Suppress("UnusedReceiverParameter")
public fun Krayon.addFontAssociation(
    fontName: String,
    @FontRes fontRes: Int,
) {
    fontResources[fontName] = fontRes
}
