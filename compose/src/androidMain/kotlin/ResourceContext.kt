package com.juul.krayon.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

public actual typealias ResourceContext = Context

@Composable
internal actual fun rememberResourceContext(): ResourceContext = LocalContext.current
