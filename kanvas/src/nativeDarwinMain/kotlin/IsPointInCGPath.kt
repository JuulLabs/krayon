package com.juul.krayon.kanvas

import platform.CoreGraphics.CGPathContainsPoint
import platform.CoreGraphics.CGPointMake

public object IsPointInCGPath : IsPointInPath {

    override fun isPointInPath(transform: Transform, path: Path, x: Float, y: Float): Boolean {
        return path.withCGPath { cgPath ->
            CGPathContainsPoint(
                cgPath,
                m = transform.asCGAffineTransform(),
                CGPointMake(x.toDouble(), y.toDouble()),
                eoFill = false,
            )
        }
    }
}
