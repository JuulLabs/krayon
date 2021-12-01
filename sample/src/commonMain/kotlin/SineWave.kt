package com.juul.krayon.sample

import com.juul.krayon.color.darkSlateBlue
import com.juul.krayon.color.steelBlue
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.shape.line
import kotlin.math.PI
import kotlin.math.sin

/** Sample loosely follows https://bl.ocks.org/mbostock/0533f44f2cfabecc5e3a, but is not yet feature complete. */
internal fun <PAINT, PATH> Kanvas<PAINT, PATH>.renderSineWave(samples: Int = 50) {
    // Generate a sine wave, with missing data every fifth point
    val data = (0 until samples).map { i ->
        val proportion = i.toFloat() / (samples - 1)
        val x = (2 * PI * proportion).toFloat()
        Point(x, y = sin(x)).takeUnless { i % 5 == 0 }
    }

    val x = scale().domain(0f, 2 * PI.toFloat()).range(10f, width - 10)
    val y = scale().domain(-1f, 1f).range(height - 10, 0f + 10)

    val line = line<Point>()
        .x { (p) -> x.scale(p.x) }
        .y { (p) -> y.scale(p.y) }

    drawPath(buildPath(line.render(data.filterNotNull())), buildPaint(Paint.Stroke(darkSlateBlue, 0.5f, dash = Paint.Stroke.Dash.Pattern(5f, 5.5f))))
    drawPath(buildPath(line.render(data)), buildPaint(Paint.Stroke(steelBlue, 1f)))
}
