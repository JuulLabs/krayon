package com.juul.krayon.sample.updaters

import com.juul.krayon.color.lerp
import com.juul.krayon.color.red
import com.juul.krayon.color.steelBlue
import com.juul.krayon.element.RectangleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.interpolate.interpolator
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import com.juul.krayon.transition.attribute
import com.juul.krayon.transition.delay
import com.juul.krayon.transition.duration
import com.juul.krayon.transition.ease
import com.juul.krayon.transition.easeCubicInOut
import com.juul.krayon.transition.transition
import com.juul.krayon.transition.tween

private val coolColor = steelBlue
private val warmColor = red

/**
 * An animated bar chart demonstrating transitions. Each time the [data] changes, bars animate their
 * height (via [attribute]) and fill color (via a custom [tween]) towards the new values, with a
 * staggered per-bar [delay]. New bars grow up from the baseline.
 */
fun animatedBarChart(root: RootElement, width: Float, height: Float, data: List<Float>) {
    val margin = 12f
    val innerWidth = width - margin * 2
    val innerHeight = height - margin * 2
    if (data.isEmpty() || innerWidth <= 0f || innerHeight <= 0f) return

    val y = scale()
        .domain(0f, 1f)
        .range(innerHeight, 0f)

    val bandWidth = innerWidth / data.size
    val barPadding = bandWidth * 0.1f

    val body = root.asSelection()
        .selectAll(TransformElement.withKind("body"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "body" } }
        .each { transform = Transform.Translate(margin, margin) }

    val bars = body.selectAll(RectangleElement)
        .data(data)
        .join {
            append(RectangleElement).each { (_, index) ->
                left = index * bandWidth + barPadding
                right = (index + 1) * bandWidth - barPadding
                top = innerHeight
                bottom = innerHeight
                paint = Paint.Fill(coolColor)
            }
        }

    // Horizontal layout snaps instantly (via each); only height and color animate (via transition).
    bars.each { (_, index) ->
        left = index * bandWidth + barPadding
        right = (index + 1) * bandWidth - barPadding
        bottom = innerHeight
    }

    bars.transition("bars")
        .duration(750)
        .ease(easeCubicInOut)
        .delay { (_, index) -> index * 30L }
        .attribute(RectangleElement::top) { (value) -> y.scale(value) }
        .tween("fill") {
            val value = this.data as Float
            val target = lerp(coolColor, warmColor, value)
            val start = (paint as? Paint.Fill)?.color ?: coolColor
            val interpolate = interpolator(start, target)
            ({ fraction -> paint = Paint.Fill(interpolate.interpolate(fraction)) })
        }
}
