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
import com.juul.krayon.color.lerp
import com.juul.krayon.color.steelBlue
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.components.ValueSlider
import com.juul.krayon.documentation.features.gallery.DonutDemo
import com.juul.krayon.element.RectangleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.sansSerif
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll

private val swatchLabelPaint = Paint.Text(black, 12f, Paint.Text.Alignment.Center, Font(sansSerif))

/** Three swatches: two fixed endpoints and the [lerp] blend between them. */
private fun lerpChart(root: RootElement, width: Float, height: Float, percent: Float) {
    val swatchSize = minOf(height * 0.5f, 72f)
    val swatchTop = height * 0.2f
    val blended = lerp(crimson, steelBlue, percent)

    val swatches = listOf(
        Triple("crimson", crimson, width * 0.15f),
        Triple("lerp(crimson, steelBlue, ${(percent * 100).toInt() / 100f}f)", blended, width * 0.5f),
        Triple("steelBlue", steelBlue, width * 0.85f),
    )

    root.asSelection()
        .selectAll(RectangleElement)
        .data(swatches)
        .join(RectangleElement)
        .each { (swatch) ->
            val (_, color, center) = swatch
            left = center - swatchSize / 2f
            right = center + swatchSize / 2f
            top = swatchTop
            bottom = swatchTop + swatchSize
            paint = Paint.Fill(color)
        }

    root.asSelection()
        .selectAll(TextElement.withKind("label"))
        .data(swatches)
        .join { append(TextElement).each { kind = "label" } }
        .each { (swatch) ->
            text = swatch.first
            x = swatch.third
            y = swatchTop + swatchSize + 20f
            paint = swatchLabelPaint
        }
}

@Composable
fun ColorScreen() {
    Column {
        Text("Color & interpolation", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            *The Krayon equivalents of [d3-color](https://d3js.org/d3-color) and
            [d3-interpolate](https://d3js.org/d3-interpolate).*

            ## Color

            `Color` is a lightweight value class over a packed ARGB `Int` — allocation-free and
            usable from any platform:

            ```kotlin
            val custom = Color(0xFF4682B4.toInt())     // from ARGB
            val parsed = "#4682b4".toColor()           // from CSS-style hex strings
            val faded = steelBlue.copy(alpha = 0x99)   // component-wise copy
            ```

            All ~150 CSS named colors ship as constants (`steelBlue`, `crimson`, `seaGreen`, ...),
            and `lerp` blends two colors channel-by-channel:

            ```kotlin
            val highlight = lerp(baseColor, white, 0.25f)   // 25% towards white
            ```
            """,
            Modifier.padding(vertical = 8.dp),
        )
        LerpDemo()
        MarkdownBlock(
            """
            ## Interpolators

            The `interpolate` module defines the `Interpolator` abstraction that scales are built
            on: `interpolate(fraction)` maps `[0,1]` onto `[start,stop]`, and an `Inverter` maps
            back. Built-in interpolators cover numbers, time types, and colors — which is exactly
            why a scale's range can be a `List<Color>` (see [Scales](#concepts/scales)).

            ## Gradients

            For continuous color *within a single shape*, use gradient paints from the `kanvas`
            module — linear, radial, or sweep, each with arbitrary color stops:

            ```kotlin
            val paint = Paint.Gradient.Linear(
                startX = 0f, startY = 0f,
                endX = 0f, endY = innerHeight,
                Paint.Gradient.Stop(0f, steelBlue.copy(alpha = 0x99)),
                Paint.Gradient.Stop(1f, transparent),
            )
            ```

            The donut below shades each slice with a sweep gradient (`Paint.Gradient.Sweep`)
            blended from the base color towards white and black:
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DemoCard(height = 300.dp) {
            DonutDemo(Modifier.fillMaxSize())
        }
        MarkdownBlock(
            """
            ## Differences from D3

            Color interpolation is currently RGB-space only — perceptual spaces (HCL, Lab) and the
            d3-scale-chromatic palettes are not yet available. Build ramps by hand with multi-stop
            color scales, as in the gallery's [color scales](#gallery/color-scales) example.
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
private fun LerpDemo() {
    val percent = remember { mutableStateOf(0.5f) }
    DemoCard(height = 200.dp, caption = "lerp blends channel-by-channel between the two endpoint colors.") {
        Column(Modifier.fillMaxSize()) {
            ElementView(percent, ::lerpChart, Modifier.fillMaxWidth().weight(1f))
            ValueSlider("Percent", 0f, 1f, percent)
        }
    }
}
