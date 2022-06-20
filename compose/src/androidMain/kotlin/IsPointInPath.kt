package com.juul.krayon.compose

import com.juul.krayon.kanvas.IsPointInPath
import com.juul.krayon.kanvas.ScaledIsPointInPath

internal actual fun isPointInPath(): IsPointInPath = ScaledIsPointInPath(1f)
