package com.juul.krayon.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// TODO: Implement caching. But since desktops are fast, this is less of a worry than on Android.
public actual class ResourceCache

@Composable
internal actual fun rememberResourceCache(): ResourceCache = remember { ResourceCache() }
