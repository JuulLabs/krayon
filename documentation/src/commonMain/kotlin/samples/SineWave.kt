package com.juul.krayon.documentation.samples

import com.juul.krayon.axis.axisBottom
import com.juul.krayon.axis.axisLeft
import com.juul.krayon.axis.call
import com.juul.krayon.color.steelBlue
import com.juul.krayon.element.GroupElement
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.extent
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import com.juul.krayon.shape.line
import kotlin.math.PI
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

/**
 * Emits a rolling window of sine wave samples, as fast as they are collected.
 * `ElementView` collects one emission per frame, so this animates at the device frame rate.
 */
fun movingSineWave(
    period: Duration = 10.seconds,
    samples: Int = 50,
): Flow<List<Point>> = flow {
    var offset = 0f
    var time = TimeSource.Monotonic.markNow()
    while (currentCoroutineContext().isActive) {
        emit(
            List(samples) { i ->
                val x = offset + (2 * PI * i / (samples - 1)).toFloat()
                Point(x, sin(x))
            },
        )
        offset += ((time.elapsedNow() / period) * 2 * PI).toFloat()
        time = TimeSource.Monotonic.markNow()
    }
}

private val wavePaint = Paint.Stroke(steelBlue, 2f, join = Paint.Stroke.Join.Round)

/** Redrawing the same elements with fresh data each frame is Krayon's version of a transition. */
fun sineWaveChart(root: RootElement, width: Float, height: Float, data: List<Point>) {
    val margin = Margin(top = 16f, right = 16f, bottom = 32f, left = 40f)
    val innerWidth = width - margin.left - margin.right
    val innerHeight = height - margin.top - margin.bottom

    val x = scale()
        .domain(data.extent { it.x })
        .range(0f, innerWidth)
    val y = scale()
        .domain(-1f, 1f)
        .range(innerHeight, 0f)

    val waveLine = line<Point>()
        .x { (point) -> x.scale(point.x) }
        .y { (point) -> y.scale(point.y) }

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

    // The "wave" kind keeps this join from matching the axes' internal PathElements.
    body.selectAll(PathElement.withKind("wave"))
        .data(listOf(data))
        .join { append(PathElement).each { kind = "wave" } }
        .each { (points) ->
            path = waveLine.render(points)
            paint = wavePaint
        }
}
