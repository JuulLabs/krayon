package com.juul.krayon.documentation.features.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.samples.areaChart
import com.juul.krayon.documentation.samples.barChart
import com.juul.krayon.documentation.samples.browserShares
import com.juul.krayon.documentation.samples.cityTemperatures
import com.juul.krayon.documentation.samples.colorScales
import com.juul.krayon.documentation.samples.flowers
import com.juul.krayon.documentation.samples.histogram
import com.juul.krayon.documentation.samples.horizontalBarChart
import com.juul.krayon.documentation.samples.hourlyTemperature
import com.juul.krayon.documentation.samples.languageSpeakers
import com.juul.krayon.documentation.samples.letterFrequency
import com.juul.krayon.documentation.samples.lineChart
import com.juul.krayon.documentation.samples.missingDataLineChart
import com.juul.krayon.documentation.samples.monthlyRainfall
import com.juul.krayon.documentation.samples.multiSeriesLineChart
import com.juul.krayon.documentation.samples.normalSamples
import com.juul.krayon.documentation.samples.patchySineWave
import com.juul.krayon.documentation.samples.pieChart
import com.juul.krayon.documentation.samples.quarterlySales
import com.juul.krayon.documentation.samples.scatterplot
import com.juul.krayon.documentation.samples.stackedBarChart

/** A gallery entry: a live chart, its description, and the source code that produces it. */
class Sample(
    val id: String,
    val title: String,
    /** Markdown prose shown on the detail page. */
    val description: String,
    /** Name of the equivalent example in D3's gallery, if any. */
    val d3Counterpart: String? = null,
    /** URL of the equivalent D3 example (typically on observablehq.com). */
    val d3Url: String? = null,
    /** Krayon modules this sample demonstrates. */
    val modules: List<String>,
    /** Source file shown on the detail page, relative to `src/commonMain/kotlin/samples`. */
    val codePath: String,
    /** Renders the live chart. Pass `withControls = true` to include interactive parameters. */
    val content: @Composable (modifier: Modifier, withControls: Boolean) -> Unit,
)

val samples: List<Sample> = listOf(
    Sample(
        id = "bar-chart",
        title = "Bar chart",
        description = """
            The frequency of letters in English text, drawn with one `RectangleElement` per letter.
            A continuous [scale](../concepts/scales) maps frequency to bar height, and an
            [axis](../concepts/axes) provides reference lines. Krayon has no band scale yet, so bar
            positions are computed with simple index math.
        """.trimIndent(),
        d3Counterpart = "Bar chart",
        d3Url = "https://observablehq.com/@d3/bar-chart/2",
        modules = listOf("element", "selection", "scale", "axis"),
        codePath = "BarChart.kt",
        content = { modifier, _ -> ElementView({ letterFrequency }, ::barChart, modifier) },
    ),
    Sample(
        id = "horizontal-bar-chart",
        title = "Horizontal bar chart",
        description = """
            The bar chart, transposed: the value scale runs along the x-axis and categories are
            stacked vertically, leaving comfortable room for the category labels.
        """.trimIndent(),
        d3Counterpart = "Horizontal bar chart",
        d3Url = "https://observablehq.com/@d3/horizontal-bar-chart/2",
        modules = listOf("element", "selection", "scale", "axis"),
        codePath = "HorizontalBarChart.kt",
        content = { modifier, _ -> ElementView({ languageSpeakers }, ::horizontalBarChart, modifier) },
    ),
    Sample(
        id = "line-chart",
        title = "Line chart",
        description = """
            Hourly temperature over a day. The `line()` generator from the `shape` module converts
            data points into a `Path`, mirroring `d3.line()`.
        """.trimIndent(),
        d3Counterpart = "Line chart",
        d3Url = "https://observablehq.com/@d3/line-chart/2",
        modules = listOf("shape", "element", "selection", "scale", "axis"),
        codePath = "LineChart.kt",
        content = { modifier, _ -> ElementView({ hourlyTemperature }, ::lineChart, modifier) },
    ),
    Sample(
        id = "missing-data",
        title = "Line chart, missing data",
        description = """
            `null` data points break the solid line into segments — the equivalent of
            `line.defined()` in D3. A second, dashed path is rendered from the same dataset with the
            gaps filtered out, and circles mark the samples that exist.
        """.trimIndent(),
        d3Counterpart = "Line chart, missing data",
        d3Url = "https://observablehq.com/@d3/line-with-missing-data/2",
        modules = listOf("shape", "element", "selection", "scale", "axis"),
        codePath = "MissingDataLineChart.kt",
        content = { modifier, _ -> ElementView({ patchySineWave }, ::missingDataLineChart, modifier) },
    ),
    Sample(
        id = "multi-series-line-chart",
        title = "Multi-series line chart",
        description = """
            Monthly temperatures for three cities. Scales work over `LocalDateTime` domains, and the
            time-aware axis picks sensible ticks. Each series is labeled directly at the end of its
            line, avoiding a separate legend.
        """.trimIndent(),
        d3Counterpart = "Multi-line chart",
        d3Url = "https://observablehq.com/@d3/multi-line-chart/2",
        modules = listOf("shape", "time", "scale", "axis"),
        codePath = "MultiSeriesLineChart.kt",
        content = { modifier, _ -> ElementView({ cityTemperatures }, ::multiSeriesLineChart, modifier) },
    ),
    Sample(
        id = "area-chart",
        title = "Area chart",
        description = """
            Monthly rainfall as an `area()` fill under a `line()` stroke. The fill uses a linear
            gradient `Paint` that fades towards the baseline.
        """.trimIndent(),
        d3Counterpart = "Area chart",
        d3Url = "https://observablehq.com/@d3/area-chart/2",
        modules = listOf("shape", "kanvas", "scale", "axis"),
        codePath = "AreaChart.kt",
        content = { modifier, _ -> ElementView({ monthlyRainfall }, ::areaChart, modifier) },
    ),
    Sample(
        id = "scatterplot",
        title = "Scatterplot",
        description = """
            An iris-like dataset of three species, one `CircleElement` per flower, colored by
            species with translucent fills.
        """.trimIndent(),
        d3Counterpart = "Scatterplot",
        d3Url = "https://observablehq.com/@d3/scatterplot/2",
        modules = listOf("element", "selection", "scale", "axis"),
        codePath = "Scatterplot.kt",
        content = { modifier, _ -> ElementView({ flowers }, ::scatterplot, modifier) },
    ),
    Sample(
        id = "pie-chart",
        title = "Pie chart",
        description = """
            Browser market share. `pie()` computes the angles, `arc()` turns each slice into a
            `Path`, and labels sit at each slice's centroid — the same division of labor as
            `d3.pie()` and `d3.arc()`.
        """.trimIndent(),
        d3Counterpart = "Pie chart",
        d3Url = "https://observablehq.com/@d3/pie-chart/2",
        modules = listOf("shape", "element", "selection"),
        codePath = "PieChart.kt",
        content = { modifier, _ -> ElementView({ browserShares }, ::pieChart, modifier) },
    ),
    Sample(
        id = "donut-chart",
        title = "Donut chart",
        description = """
            Every knob of the arc generator — inner radius, corner radius, pad angle, and the
            overall start/end angles — bound to interactive controls. Sweep gradients give each
            slice a subtle sheen.
        """.trimIndent(),
        d3Counterpart = "Donut chart",
        d3Url = "https://observablehq.com/@d3/donut-chart/2",
        modules = listOf("shape", "kanvas", "interpolate"),
        codePath = "DonutChart.kt",
        content = { modifier, withControls -> DonutDemo(modifier, withControls) },
    ),
    Sample(
        id = "stacked-bar-chart",
        title = "Stacked bar chart",
        description = """
            Quarterly sales by product line. Krayon has no stack generator yet, so the layers are
            stacked by accumulating a running total per quarter — a few lines of ordinary Kotlin.
        """.trimIndent(),
        d3Counterpart = "Stacked bar chart",
        d3Url = "https://observablehq.com/@d3/stacked-bar-chart/2",
        modules = listOf("element", "selection", "scale", "axis"),
        codePath = "StackedBarChart.kt",
        content = { modifier, _ -> ElementView({ quarterlySales }, ::stackedBarChart, modifier) },
    ),
    Sample(
        id = "histogram",
        title = "Histogram",
        description = """
            500 samples of an approximately normal distribution, binned with the Kotlin standard
            library's `groupBy` in place of `d3.bin()`.
        """.trimIndent(),
        d3Counterpart = "Histogram",
        d3Url = "https://observablehq.com/@d3/histogram/2",
        modules = listOf("element", "selection", "scale", "axis"),
        codePath = "Histogram.kt",
        content = { modifier, _ -> ElementView({ normalSamples }, ::histogram, modifier) },
    ),
    Sample(
        id = "treemap",
        title = "Treemap",
        description = """
            A package-size hierarchy laid out by the `hierarchy` module's `Treemap`, with a choice
            of tiling method: `Squarify` (D3's default) or `SliceAndDice`.
        """.trimIndent(),
        d3Counterpart = "Treemap",
        d3Url = "https://observablehq.com/@d3/treemap/2",
        modules = listOf("hierarchy", "element", "selection"),
        codePath = "TreemapChart.kt",
        content = { modifier, withControls -> TreemapDemo(modifier, withControls) },
    ),
    Sample(
        id = "gauge",
        title = "Radial gauge",
        description = """
            Two arcs over the same angle range make a gauge: a light full-length track, and a
            colored fill whose sweep is proportional to the value.
        """.trimIndent(),
        modules = listOf("shape", "element", "selection"),
        codePath = "GaugeChart.kt",
        content = { modifier, withControls -> GaugeDemo(modifier, withControls) },
    ),
    Sample(
        id = "animated-sine-wave",
        title = "Animated sine wave",
        description = """
            Krayon's take on animation: the chart is a pure function of its data, so a `Flow` that
            emits new data every frame produces a smoothly scrolling chart. `ElementView` collects
            one emission per display frame.
        """.trimIndent(),
        modules = listOf("compose", "shape", "scale", "axis"),
        codePath = "SineWave.kt",
        content = { modifier, _ -> SineWaveDemo(modifier) },
    ),
    Sample(
        id = "interactive-treemap",
        title = "Interactive treemap",
        description = """
            Hover to highlight, click to select (click again to deselect). Handlers on each
            `RectangleElement` feed UI events back into the state flows that drive the chart, so
            interaction is just more data.
        """.trimIndent(),
        modules = listOf("element", "hierarchy", "compose"),
        codePath = "InteractiveTreemap.kt",
        content = { modifier, _ -> InteractiveTreemapDemo(modifier) },
    ),
    Sample(
        id = "color-scales",
        title = "Color scales",
        description = """
            A `ContinuousScale` can interpolate over any range with an `Interpolator` — including
            `Color`. Each ramp below samples a scale whose range is a list of colors, mirroring
            D3's sequential and diverging color scales.
        """.trimIndent(),
        d3Counterpart = "Color legend",
        d3Url = "https://observablehq.com/@d3/color-legend",
        modules = listOf("color", "scale", "interpolate"),
        codePath = "ColorScales.kt",
        content = { modifier, _ -> ElementView({ }, ::colorScales, modifier) },
    ),
)

fun sampleById(id: String?): Sample? = samples.firstOrNull { it.id == id }
