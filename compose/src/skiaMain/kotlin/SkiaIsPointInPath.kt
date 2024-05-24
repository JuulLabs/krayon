package com.juul.krayon.compose

import androidx.compose.ui.graphics.asSkiaPath
import com.juul.krayon.kanvas.IsPointInPath
import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.Transform.InOrder
import com.juul.krayon.kanvas.Transform.Rotate
import com.juul.krayon.kanvas.Transform.Scale
import com.juul.krayon.kanvas.Transform.Skew
import com.juul.krayon.kanvas.Transform.Translate
import org.jetbrains.skia.Matrix33

internal actual fun isPointInPath(): IsPointInPath = SkiaIsPointInPath()

public class SkiaIsPointInPath : IsPointInPath {
    override fun isPointInPath(transform: Transform, path: Path, x: Float, y: Float): Boolean {
        val skiaPath = path.get(ComposePathMarker).asSkiaPath()
        skiaPath.transform(transform.asMatrix())
        return skiaPath.contains(x, y)
    }
}

private fun Transform.asMatrix(): Matrix33 = when (this) {
    is InOrder -> {
        var buffer = Matrix33.IDENTITY
        transformations.forEach { transform ->
            buffer = buffer.makeConcat(transform.asMatrix())
        }
        buffer
    }
    is Scale -> if (pivotX == 0f && pivotY == 0f) {
        Matrix33.makeScale(horizontal, vertical)
    } else {
        InOrder(Translate(pivotX, pivotY), Scale(horizontal, vertical), Translate(-pivotX, -pivotY)).asMatrix()
    }
    is Rotate -> if (pivotX == 0f && pivotY == 0f) {
        Matrix33.makeRotate(degrees)
    } else {
        Matrix33.makeRotate(degrees, pivotX, pivotY)
    }
    is Translate -> Matrix33.makeTranslate(horizontal, vertical)
    is Skew -> Matrix33.makeSkew(horizontal, vertical)
    else -> error("Unreachable.")
}
