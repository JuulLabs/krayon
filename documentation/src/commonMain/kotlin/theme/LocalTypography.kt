package com.juul.krayon.documentation.theme

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalTypography: ProvidableCompositionLocal<Typography> =
    staticCompositionLocalOf { Typography.Default }
