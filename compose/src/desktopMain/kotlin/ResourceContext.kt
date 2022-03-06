package com.juul.krayon.compose

import androidx.compose.runtime.Composable

public actual abstract class ResourceContext private constructor() {
    internal object Impl : ResourceContext()
}

@Composable
internal actual fun rememberResourceContext(): ResourceContext = ResourceContext.Impl
