package com.juul.krayon.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.juul.krayon.kanvas.PaintCache

public actual class ResourceCache(
    internal val context: Context,
) {
    internal val cache: PaintCache = PaintCache(context)
}

@Composable
internal actual fun rememberResourceCache(): ResourceCache {
    val context = LocalContext.current
    return remember { ResourceCache(context) }
}
