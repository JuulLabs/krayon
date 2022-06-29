package com.juul.krayon.kanvas

import kotlinx.cinterop.CValue
import platform.CoreGraphics.CGAffineTransform
import platform.CoreGraphics.CGAffineTransformConcat

internal fun CValue<CGAffineTransform>.concat(other: CValue<CGAffineTransform>): CValue<CGAffineTransform> =
    CGAffineTransformConcat(this, other)
