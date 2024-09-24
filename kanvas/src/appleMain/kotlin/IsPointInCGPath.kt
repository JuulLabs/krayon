package com.juul.krayon.kanvas

import platform.CoreGraphics.CGAffineTransformInvert
import platform.CoreGraphics.CGPathContainsPoint
import platform.CoreGraphics.CGPointMake

public object IsPointInCGPath : IsPointInPath {

    override fun isPointInPath(transform: Transform, path: Path, x: Float, y: Float): Boolean = path.withCGPath { cgPath ->
        // Krayon passes the path transformation here, but CGPathContainsPoint can transform
        // just the point and it's actually cheaper than transforming a whole path.
        // TODO: Investigate propagating this optimization to other platforms.
        val pointTransform = CGAffineTransformInvert(transform.asCGAffineTransform())
        CGPathContainsPoint(
            cgPath,
            m = pointTransform,
            point = CGPointMake(x.toDouble(), y.toDouble()),
            eoFill = false,
        )
    }
}
