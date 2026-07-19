package com.juul.krayon.documentation.samples

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
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import com.juul.krayon.shape.arc
import com.juul.krayon.shape.pie

/** Parameters for [donutChart], typically bound to interactive controls. */
data class DonutChart(
    val values: List<Float> = listOf(1f, 2f, 3f, 4f, 5f, 6f),
    val startAngle: Float = 0f,
    val endAngle: Float = 6.2832f,
    val cornerRadius: Float = 4f,
    val padAngle: Float = 0.02f,
    val innerRadiusFraction: Float = 0.5f,
)

private val paints = listOf(red, blue, green, darkMagenta, darkCyan, olive)
    .map { color ->
        val gradient = Paint.Gradient.Sweep(
            centerX = 0f,
            centerY = 0f,
            Paint.Gradient.Stop(0f, lerp(color, white, 0.75f)),
            Paint.Gradient.Stop(1f, lerp(color, black, 0.25f)),
        )
        val stroke = Paint.Stroke(black, 0.5f, join = Paint.Stroke.Join.Round)
        Paint.GradientAndStroke(gradient, stroke)
    }

fun donutChart(root: RootElement, width: Float, height: Float, data: DonutChart) {
    // Offset of 1px just so stroke doesn't run outside the edge of the chart.
    val outerRadius = minOf(width, height) / 2f - 1f

    val donutArc = arc(outerRadius, innerRadius = outerRadius * data.innerRadiusFraction)
        .cornerRadius(data.cornerRadius)

    val donutPie = pie()
        .startAngle(data.startAngle)
        .endAngle(data.endAngle)
        .padAngle(data.padAngle)

    val center = root.asSelection()
        .selectAll(TransformElement)
        .data(listOf(null))
        .join(TransformElement)
        .each { transform = Transform.Translate(width / 2f, height / 2f) }

    center.selectAll(PathElement)
        .data(donutPie(data.values))
        .join(PathElement)
        .each { (slice, index) ->
            path = donutArc(slice)
            paint = paints[index % paints.size]
        }
}
