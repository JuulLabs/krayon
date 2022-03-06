package com.juul.krayon.kanvas.compose

import com.juul.krayon.color.Color
import androidx.compose.ui.graphics.Color as ComposeColor

internal fun Color.asComposeColor(): ComposeColor = ComposeColor(argb)
