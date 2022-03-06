package com.juul.krayon.compose

import androidx.compose.runtime.Composable

// TODO: Figure out why this doesn't compile when marked internal instead of public
public expect abstract class ResourceContext

@Composable
internal expect fun rememberResourceContext(): ResourceContext
