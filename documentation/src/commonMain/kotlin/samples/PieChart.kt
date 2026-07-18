package com.juul.krayon.documentation.samples

import com.juul.krayon.color.Color
import com.juul.krayon.color.cornflowerBlue
import com.juul.krayon.color.crimson
import com.juul.krayon.color.darkCyan
import com.juul.krayon.color.goldenrod
import com.juul.krayon.color.mediumPurple
import com.juul.krayon.color.seaGreen
import com.juul.krayon.color.white
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.sansSerif
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import com.juul.krayon.shape.arc
import com.juul.krayon.shape.pie
import kotlin.math.cos
import kotlin.math.sin

data class BrowserShare(
    val browser: String,
    val share: Float,
    val color: Color,
)

/** Approximate browser market share. */
val browserShares: List<BrowserShare> = listOf(
    BrowserShare("Chrome", 65.1f, cornflowerBlue),
    BrowserShare("Safari", 18.2f, crimson),
    BrowserShare("Edge", 5.3f, seaGreen),
    BrowserShare("Firefox", 2.7f, goldenrod),
    BrowserShare("Opera", 2.2f, mediumPurple),
    BrowserShare("Other", 6.5f, darkCyan),
)

private val labelPaint = Paint.Text(white, 12f, Paint.Text.Alignment.Center, Font(sansSerif))

fun pieChart(root: RootElement, width: Float, height: Float, data: List<BrowserShare>) {
    val radius = minOf(width, height) / 2f - 8f

    // The pie generator computes start/end angles for each datum...
    val sharePie = pie().value<BrowserShare> { it.share }

    // ...and the arc generator turns those angles into a Path.
    val shareArc = arc(outerRadius = radius)

    val center = root.asSelection()
        .selectAll(TransformElement)
        .data(listOf(null))
        .join(TransformElement)
        .each { transform = Transform.Translate(width / 2f, height / 2f) }

    val slices = sharePie(data)

    center.selectAll(PathElement)
        .data(slices)
        .join(PathElement)
        .each { (slice) ->
            path = shareArc(slice)
            paint = Paint.FillAndStroke(
                Paint.Fill(slice.data.color),
                Paint.Stroke(white, 2f),
            )
        }

    // Position labels along the centroid angle of each slice.
    // Angle 0 is at 12 o'clock, so subtract π/2 for the usual cos/sin math.
    center.selectAll(TextElement)
        .data(slices.filter { it.data.share >= 3f })
        .join(TextElement)
        .each { (slice) ->
            val angle = (slice.startAngle + slice.endAngle) / 2f - 1.5708f
            text = slice.data.browser
            x = cos(angle) * radius * 0.7f
            y = sin(angle) * radius * 0.7f + 4f
            paint = labelPaint
        }
}
