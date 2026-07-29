package com.juul.krayon.documentation.samples

import com.juul.krayon.axis.axisBottom
import com.juul.krayon.axis.axisLeft
import com.juul.krayon.axis.call
import com.juul.krayon.color.steelBlue
import com.juul.krayon.element.GroupElement
import com.juul.krayon.element.RectangleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
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
import kotlin.math.floor
import kotlin.random.Random

/** 500 deterministic samples of an approximately normal distribution (mean 0, stddev 1). */
val normalSamples: List<Float> = run {
    val random = Random(seed = 47)
    // Averaging uniform samples approximates a normal distribution (central limit theorem).
    List(500) { (1..12).sumOf { random.nextDouble() }.toFloat() - 6f }
}

private val barPaint = Paint.Fill(steelBlue)

fun histogram(root: RootElement, width: Float, height: Float, data: List<Float>) {
    val margin = Margin(top = 16f, right = 16f, bottom = 32f, left = 40f)
    val innerWidth = width - margin.left - margin.right
    val innerHeight = height - margin.top - margin.bottom

    // Bin the samples: Krayon has no bin generator (yet), but the standard library gets close.
    val binWidth = 0.5f
    val bins = data
        .groupBy { sample -> floor(sample / binWidth) * binWidth }
        .toList()
        .sortedBy { (start, _) -> start }

    val x = scale()
        .domain(bins.first().first, bins.last().first + binWidth)
        .range(0f, innerWidth)
    val y = scale()
        .domain(0f, bins.maxOf { (_, samples) -> samples.size.toFloat() })
        .range(innerHeight, 0f)

    val body = root.asSelection()
        .selectAll(TransformElement.withKind("body"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "body" } }
        .each { transform = Transform.Translate(margin.left, margin.top) }

    body.selectAll(TransformElement.withKind("x-axis"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "x-axis" } }
        .each { transform = Transform.Translate(vertical = innerHeight) }
        .call(axisBottom(x))

    body.selectAll(GroupElement.withKind("y-axis"))
        .data(listOf(null))
        .join { append(GroupElement).each { kind = "y-axis" } }
        .call(axisLeft(y))

    body.selectAll(RectangleElement)
        .data(bins)
        .join(RectangleElement)
        .each { (bin) ->
            val (binStart, samples) = bin
            left = x.scale(binStart) + 1f
            right = x.scale(binStart + binWidth) - 1f
            top = y.scale(samples.size.toFloat())
            bottom = innerHeight
            paint = barPaint
        }
}
