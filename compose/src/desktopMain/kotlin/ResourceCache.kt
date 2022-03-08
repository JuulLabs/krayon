package com.juul.krayon.compose

import androidx.compose.runtime.Composable

// TODO: Implement caching. But since desktops are fast, this is less of a worry than on Android.
public actual class ResourceCache

@Composable
internal actual fun rememberResourceCache(): ResourceCache = ResourceCache()
