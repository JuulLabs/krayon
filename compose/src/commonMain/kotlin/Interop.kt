package com.juul.krayon.compose

import androidx.compose.ui.graphics.toArgb
import com.juul.krayon.color.Color
import androidx.compose.ui.graphics.Color as ComposeColor

public fun Color.toCompose(): ComposeColor = ComposeColor(argb)

public fun ComposeColor.toKrayon(): Color = Color(toArgb())
