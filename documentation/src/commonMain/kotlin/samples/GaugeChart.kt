package com.juul.krayon.documentation.samples

import com.juul.krayon.color.black
import com.juul.krayon.color.crimson
import com.juul.krayon.color.goldenrod
import com.juul.krayon.color.lightGray
import com.juul.krayon.color.seaGreen
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
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
import kotlin.math.roundToInt

/** A gauge sweeps three quarters of a circle, from 7:30 (-3π/4) to 4:30 (+3π/4) on a clock face. */
private const val GAUGE_START = -2.3562f
private const val GAUGE_END = 2.3562f

private val valuePaint = Paint.Text(black, 32f, Paint.Text.Alignment.Center, Font(sansSerif))

fun gaugeChart(root: RootElement, width: Float, height: Float, fraction: Float) {
    val radius = minOf(width, height) / 2f - 8f

    val gaugeArc = arc(outerRadius = radius, innerRadius = radius * 0.75f)
        .cornerRadius((radius * 0.25f) / 2f)

    // The gauge is two arcs over the same angle scale: a full track, and a partial fill.
    val track = GAUGE_START to GAUGE_END
    val fill = GAUGE_START to GAUGE_START + (GAUGE_END - GAUGE_START) * fraction.coerceIn(0f, 1f)

    val fillColor = when {
        fraction < 0.4f -> crimson
        fraction < 0.7f -> goldenrod
        else -> seaGreen
    }

    val center = root.asSelection()
        .selectAll(TransformElement)
        .data(listOf(null))
        .join(TransformElement)
        .each { transform = Transform.Translate(width / 2f, height / 2f) }

    center.selectAll(PathElement.withKind("track"))
        .data(listOf(track))
        .join { append(PathElement).each { kind = "track" } }
        .each { (angles) ->
            path = gaugeArc(angles.first, angles.second, 0f)
            paint = Paint.Fill(lightGray)
        }

    center.selectAll(PathElement.withKind("fill"))
        .data(listOf(fill))
        .join { append(PathElement).each { kind = "fill" } }
        .each { (angles) ->
            path = gaugeArc(angles.first, angles.second, 0f)
            paint = Paint.Fill(fillColor)
        }

    center.selectAll(TextElement)
        .data(listOf(fraction))
        .join(TextElement)
        .each { (value) ->
            text = "${(value * 100).roundToInt()}%"
            y = 12f
            paint = valuePaint
        }
}
