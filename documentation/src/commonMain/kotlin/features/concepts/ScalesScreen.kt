package com.juul.krayon.documentation.features.concepts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.color.black
import com.juul.krayon.color.crimson
import com.juul.krayon.color.steelBlue
import com.juul.krayon.color.white
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.components.ValueSlider
import com.juul.krayon.documentation.samples.colorScales
import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.LineElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.sansSerif
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import kotlin.math.roundToInt

private val labelFont = Font(sansSerif)
private val trackPaint = Paint.Stroke(black, 1f)
private val mappingPaint = Paint.Stroke(crimson, 1.5f, dash = Paint.Stroke.Dash.Pattern(4f, 4f))

/**
 * Visualizes what `scale().domain(0, 100).range(width, 0)` does to a single input value:
 * the top track is the domain, the bottom track is the (inverted) pixel range, and the dashed
 * line connects the input to its scaled output.
 */
private fun scaleMappingChart(root: RootElement, width: Float, height: Float, input: Float) {
    val margin = 48f
    val innerWidth = width - margin * 2f

    // The scale being demonstrated. Note the inverted range: 0 maps to the right edge.
    val demoScale = scale()
        .domain(0f, 100f)
        .range(innerWidth, 0f)

    // Where a domain value sits on the top track (plain left-to-right positioning).
    val domainPosition = scale()
        .domain(0f, 100f)
        .range(0f, innerWidth)

    val output = demoScale.scale(input)
    val topY = height * 0.32f
    val bottomY = height * 0.68f

    val body = root.asSelection()
        .selectAll(TransformElement.withKind("body"))
        .data(listOf(null))
        .join { append(TransformElement).each { kind = "body" } }
        .each { transform = Transform.Translate(horizontal = margin) }

    // Track lines, with a few reference labels each.
    body.selectAll(LineElement.withKind("track"))
        .data(listOf(topY, bottomY))
        .join {
            append(LineElement).each {
                kind = "track"
                paint = trackPaint
            }
        }
        .each { (y) ->
            startX = 0f
            endX = innerWidth
            startY = y
            endY = y
        }

    val referenceValues = listOf(0f, 25f, 50f, 75f, 100f)
    body.selectAll(TextElement.withKind("domain-label"))
        .data(referenceValues)
        .join { append(TextElement).each { kind = "domain-label" } }
        .each { (value) ->
            text = value.toInt().toString()
            x = domainPosition.scale(value)
            y = topY - 10f
            paint = Paint.Text(black, 11f, Paint.Text.Alignment.Center, labelFont)
        }

    body.selectAll(TextElement.withKind("range-label"))
        .data(referenceValues)
        .join { append(TextElement).each { kind = "range-label" } }
        .each { (value) ->
            text = "${demoScale.scale(value).roundToInt()}px"
            x = demoScale.scale(value)
            y = bottomY + 20f
            paint = Paint.Text(black, 11f, Paint.Text.Alignment.Center, labelFont)
        }

    // The mapping: input marker on the domain, output marker on the range, dashed connection.
    body.selectAll(LineElement.withKind("mapping"))
        .data(listOf(input))
        .join {
            append(LineElement).each {
                kind = "mapping"
                paint = mappingPaint
            }
        }
        .each { (value) ->
            startX = domainPosition.scale(value)
            startY = topY
            endX = demoScale.scale(value)
            endY = bottomY
        }

    body.selectAll(CircleElement)
        .data(listOf(topY to domainPosition.scale(input), bottomY to output))
        .join {
            append(CircleElement).each {
                radius = 5f
                paint = Paint.FillAndStroke(Paint.Fill(white), Paint.Stroke(steelBlue, 2f))
            }
        }
        .each { (marker) ->
            centerX = marker.second
            centerY = marker.first
        }

    body.selectAll(TextElement.withKind("readout"))
        .data(listOf(input))
        .join { append(TextElement).each { kind = "readout" } }
        .each { (value) ->
            text = "scale(${value.toInt()}) = ${demoScale.scale(value).roundToInt()}px"
            x = 0f
            y = 16f
            paint = Paint.Text(crimson, 13f, Paint.Text.Alignment.Left, labelFont)
        }
}

@Composable
fun ScalesScreen() {
    Column {
        Text("Scales", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            *The Krayon equivalent of [d3-scale](https://d3js.org/d3-scale).*

            Scales map an abstract dimension of data (the **domain**) to a visual dimension (the
            **range**) — most commonly, values to pixels.

            ```kotlin
            val y = scale()               // ContinuousScale<Float, Float>, [0,1] → [0,1]
                .domain(0f, 100f)         // data values...
                .range(innerHeight, 0f)   // ...to pixels (flipped: the origin is top-left)

            y.scale(50f)                  // == innerHeight / 2
            ```

            ## Try it

            The demo below is exactly that scale — `scale().domain(0f, 100f).range(width, 0f)` —
            with the top track showing the domain and the bottom track showing the pixel range.
            Drag the slider and watch the input cross over: because the range is inverted
            (`width, 0f`), small values land on the right and large values on the left, which is
            why y-scales in charts put `0` at the bottom.
            """,
            Modifier.padding(vertical = 8.dp),
        )
        ScalePlayground()
        MarkdownBlock(
            """
            ## Domains

            Domains work over any `Comparable` type with a built-in interpolator: `Int`, `Float`,
            `Double`, and — for time series — `Instant`, `LocalDate`, and `LocalDateTime`:

            ```kotlin
            val x = scale()
                .domain(LocalDateTime(2024, 1, 1, 0, 0), LocalDateTime(2024, 12, 1, 0, 0))
                .range(0f, innerWidth)
            ```

            Domains (and ranges) may have more than two values, splitting the scale into segments —
            the basis of diverging scales. They can also be computed from data with the `extent`
            helper (D3's `d3.extent`):

            ```kotlin
            val x = scale()
                .domain(data.extent { it.x })   // [min, max] of the data
                .range(0f, innerWidth)
            ```

            `min`, `max`, and `nice` helpers round out the toolbox.

            ## Ranges beyond pixels

            A range can be any type with an interpolator — including `Color`. Each ramp below is a
            `ContinuousScale<Float, Color>` sampled 64 times:
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DemoCard(height = 240.dp) {
            ElementView({ }, ::colorScales, Modifier.fillMaxSize())
        }
        MarkdownBlock(
            """
            ```kotlin
            val color = scale()
                .domain(0f, 0.5f, 1f)
                .range(listOf(mediumBlue, white, crimson))   // diverging color scale

            color.scale(0.25f)   // a soft blue
            ```

            ## Ticks

            Scales cooperate with tickers to produce human-friendly tick values for axes: `Float`
            and `Double` tickers pick round numbers, while the time tickers (for `Instant` and
            `LocalDateTime`) walk calendar intervals from the `time` module — seconds up to years.
            You rarely use tickers directly; the `axis` module does it for you.

            ## Differences from D3

            Krayon currently provides the equivalent of `d3.scaleLinear` and `d3.scaleTime` (via
            domain types). Band, ordinal, log, and power scales don't exist yet — for bar charts,
            compute positions with index math (see the gallery's bar charts).
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
private fun ScalePlayground() {
    val input = remember { mutableStateOf(25f) }
    DemoCard(height = 260.dp, caption = "scale().domain(0f, 100f).range(width, 0f) — input above, scaled output below.") {
        Column(Modifier.fillMaxSize()) {
            ElementView(input, ::scaleMappingChart, Modifier.fillMaxWidth().weight(1f))
            ValueSlider("Input", 0f, 100f, input)
        }
    }
}
