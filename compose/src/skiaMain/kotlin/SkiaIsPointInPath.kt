package com.juul.krayon.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asSkiaPath
import com.juul.krayon.kanvas.IsPointInPath
import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.Transform.InOrder
import com.juul.krayon.kanvas.Transform.Rotate
import com.juul.krayon.kanvas.Transform.Scale
import com.juul.krayon.kanvas.Transform.Skew
import com.juul.krayon.kanvas.Transform.Translate
import com.juul.krayon.kanvas.split

internal actual fun isPointInPath(): IsPointInPath = SkiaIsPointInPath()

public class SkiaIsPointInPath : IsPointInPath {
    override fun isPointInPath(transform: Transform, path: Path, x: Float, y: Float): Boolean {
        val inverseMatrix = transform.asMatrix().apply { invert() }
        val transformedPoint = inverseMatrix.map(Offset(x, y))
        val skiaPath = path.get(ComposePathMarker).asSkiaPath()
        return skiaPath.contains(transformedPoint.x, transformedPoint.y)
    }
}

private fun Transform.asMatrix(): Matrix {
    val buffer = Matrix()
    applyTo(buffer)
    return buffer
}

private fun Transform.applyTo(matrix: Matrix) {
    when (this) {
        is InOrder -> transformations.forEach { transform ->
            transform.applyTo(matrix)
        }

        is Rotate -> if (pivotX == 0f && pivotY == 0f) {
            matrix.rotateZ(degrees)
        } else {
            matrix.translate(pivotX, pivotY)
            matrix.rotateZ(degrees)
            matrix.translate(-pivotX, -pivotY)
        }

        is Scale -> if (pivotX == 0f && pivotY == 0f) {
            matrix.scale(horizontal, vertical)
        } else {
            matrix.translate(pivotX, pivotY)
            matrix.scale(horizontal, vertical)
            matrix.translate(-pivotX, -pivotY)
        }

        is Skew -> {
            // No helper function for skew, so eat the cost of allocation
            matrix *= Matrix().apply {
                values[Matrix.SkewX] = horizontal
                values[Matrix.SkewY] = vertical
            }
        }

        is Translate -> matrix.translate(horizontal, vertical)
    }
}
