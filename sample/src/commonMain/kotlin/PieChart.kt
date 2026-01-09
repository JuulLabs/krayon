package com.juul.krayon.sample

import com.juul.krayon.color.black
import com.juul.krayon.color.blue
import com.juul.krayon.color.darkCyan
import com.juul.krayon.color.darkMagenta
import com.juul.krayon.color.green
import com.juul.krayon.color.lerp
import com.juul.krayon.color.olive
import com.juul.krayon.color.red
import com.juul.krayon.color.white
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Paint.Gradient.Stop
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import com.juul.krayon.shape.arc
import com.juul.krayon.shape.pie

private val paints = listOf(red, blue, green, darkMagenta, darkCyan, olive)
    .map { color ->
        val gradient = Paint.Gradient.Sweep(
            centerX = 0f,
            centerY = 0f,
            Stop(0f, lerp(color, white, 0.75f)),
            Stop(1f, lerp(color, black, 0.25f)),
        )
        val stroke = Paint.Stroke(black, 0.5f, join = Paint.Stroke.Join.Round)
        Paint.GradientAndStroke(gradient, stroke)
    }

internal data class PieChart(
    val values: List<Float>,
    val startAngle: Float,
    val endAngle: Float,
    val cornerRadius: Float,
    val paddingAngle: Float,
    val innerRadius: Float,
)

internal fun pieChart(root: RootElement, width: Float, height: Float, data: PieChart) {
    // Offset of 1px just so stroke doesn't run outside the edge of the chart
    val arc = arc(minOf(width, height) / 2f - 1f, data.innerRadius)
        .cornerRadius(data.cornerRadius)

    val pie = pie()
        .startAngle(data.startAngle)
        .endAngle(data.endAngle)
        .padAngle(data.paddingAngle)

    val translate = root.asSelection()
        .selectAll(TransformElement)
        .data(listOf(null))
        .join(TransformElement)
        .each { transform = Transform.Translate(width / 2, height / 2) }

    translate.selectAll(PathElement)
        .data(pie(data.values))
        .join(PathElement)
        .each { (slice, index) ->
            paint = paints[index]
            path = arc(slice)
            onHoverChanged { a, b ->
                println("Element ${a.data} hover state $b")
            }
        }
}
