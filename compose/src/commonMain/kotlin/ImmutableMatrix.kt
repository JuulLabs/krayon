package com.juul.krayon.compose

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.split
import kotlin.jvm.JvmInline

@Immutable
@JvmInline
public value class ImmutableMatrix private constructor(
    internal val matrix: Matrix,
) {

    public constructor() : this(Matrix())

    @Stable
    public fun withTransform(density: Density, transform: Transform): ImmutableMatrix {
        val buffer = Matrix().apply { setFrom(matrix) }

        tailrec fun applyTransformation(transform: Transform) {
            when (transform) {
                is Transform.Skew -> error("Skew is unsupported by compose graphics layers.")

                is Transform.InOrder -> transform.transformations.forEach {
                    @Suppress("NON_TAIL_RECURSIVE_CALL")
                    applyTransformation(it)
                }

                is Transform.Translate -> with(density) {
                    buffer.translate(transform.horizontal.dp.toPx(), transform.vertical.dp.toPx())
                }

                is Transform.Rotate -> if (transform.isPivoted) {
                    applyTransformation(transform.split())
                } else {
                    buffer.rotateZ(transform.degrees)
                }

                is Transform.Scale -> if (transform.isPivoted) {
                    applyTransformation(transform.split())
                } else {
                    buffer.scale(transform.horizontal, transform.vertical)
                }
            }
        }

        applyTransformation(transform)
        return ImmutableMatrix(buffer)
    }
}

public fun DrawTransform.transform(matrix: ImmutableMatrix): Unit = transform(matrix.matrix)

public fun Modifier.transform(transformation: ImmutableMatrix): Modifier = drawWithContent {
    withTransform({ transform(transformation) }) {
        this@drawWithContent.drawContent()
    }
}

public fun Path.transform(matrix: ImmutableMatrix): Unit = transform(matrix.matrix)
