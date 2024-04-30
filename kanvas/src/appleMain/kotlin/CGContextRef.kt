package com.juul.krayon.kanvas

import platform.CoreGraphics.CGContextRef
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM

public fun CGContextRef.setCoordinatesInvertedVertically(height: Double) {
    CGContextTranslateCTM(this, 0.0, height)
    CGContextScaleCTM(this, 1.0, -1.0)
}
