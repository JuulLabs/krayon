package com.juul.krayon.documentation.features.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juul.krayon.documentation.components.Loading
import com.juul.krayon.documentation.components.Quote
import com.juul.krayon.documentation.features.tutorial.TutorialViewModel.Code
import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.generated.resources.class_structure
import com.juul.krayon.documentation.generated.resources.data_to_dom
import com.juul.krayon.documentation.highlight
import com.juul.krayon.documentation.theme.AppTheme
import dev.snipme.highlights.model.SyntaxTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun TutorialScreen(
    viewModel: TutorialViewModel = viewModel { TutorialViewModel() },
) {
    val code = viewModel.code.collectAsState(null).value
    if (code == null) {
        Loading()
    } else {
        val syntaxTheme = AppTheme.highlight
        Tutorial(remember(syntaxTheme) { code.highlight(syntaxTheme) })
    }
}

private data class HighlightedCode(
    val line1: AnnotatedString,
)

private fun Code.highlight(syntaxTheme: SyntaxTheme) =
    HighlightedCode(
        line1 = line1.highlight(syntaxTheme),
    )

@Composable
private fun Tutorial(code: HighlightedCode) {
    Column {
        Text("Comparison to [D3]", style = MaterialTheme.typography.h2)
        Quote {
            Text("D3 (or D3.js) is a free, open-source JavaScript library for visualizing data.")
        }
        Text("D3 (which stands for \"Data-Driven Documents\") is commonly used to have a dataset drive manipulation of the HTML [Document Object Model] (DOM).")
        Image(
            painterResource(Res.drawable.data_to_dom),
            "Data to DOM",
            contentScale = FixedScale(2f),
        )
        Text("[D3]'s [\"most central feature\" is \"data binding\"](https://stackoverflow.com/a/50143500), whereas data can drive the mutation of the HTML [DOM]. [D3] can also be used to draw to SVG or [Canvas], but faux elements (rather than HTML [DOM] elements) are created to take advantage of [D3]'s data binding capabilities.")
        Text("[D3] is undoubtedly a powerful visualization library, _but_ it only works in JavaScript environments (namely, the Web).")
        Text("Enter Krayon, a [Kotlin]™ [multiplatform] library that aims to bring powerful visualization tools to the many platforms supported by [Kotlin]™ (e.g. Android, iOS, Web).")
        Text("Elements", style = MaterialTheme.typography.h2)
        Text("Since Android and iOS don't have an HTML [DOM], Krayon provides intermediary [`Element`]s that can undergo data binding and be rendered on supported platforms.")
        Image(
            painterResource(Res.drawable.class_structure),
            "Class structure",
            contentScale = FixedScale(2f),
        )
        Text("Drawing", style = MaterialTheme.typography.h2)
        Text("To draw a simple line on an HTML [Canvas], we start by creating a [Canvas]:")
        Text(
            """
                <canvas id="canvas1" width="100" height="100" style="outline: black 1px solid;"></canvas>
                """.trimIndent(),
            style = AppTheme.typography.code,
        )
        Text("We can then perform the following in [Kotlin]™:")
        Text("1. Create a [`RootElement`]")
        Text("2. Access [`RootElement`] as a [`Selection`]")
        Text("3. Add a [`LineElement`] to the [`RootElement`]")
        Text("4. Update the start and end points of the [`LineElement`]")
        Text("5. Draw the [`RootElement`] to a [`HtmlKanvas`] (which bridges to an HTML [Canvas])")
        Text(code.line1, style = AppTheme.typography.code)
    }
}

private val markdown = """
    We can simply call into the JavaScript API produced by Kotlin (via `@JsExport`):

    ```html
    <script>sample.tutorial.setupLine1(document.getElementById("canvas1"));</script>
    ```

    This will render as:

    <canvas id="canvas1" width="100" height="100" style="outline: black 1px solid;"></canvas>
    <script>sample.tutorial.setupLine1(document.getElementById("canvas1"));</script>

    ### Data

    To draw the same line as above, but powered by a dataset, we can perform the following in
    [Kotlin]™:

    1. Create a [`RootElement`]
    2. Access [`RootElement`] as a [`Selection`]
    3. Selects all [`LineElement`]s that are children of the [`RootElement`]
    4. Associate data with the [`Selection`]
    5. Combine each element of the data with a [`LineElement`]
    6. Iterate over each item of the data (`data` is a `Pair<Point, Point>`) and [`LineElement`]s
    7. Deconstruct `data` as `start` (`Point`) and `end` (`Point`) 
    8. Assign the `Point` values to the [`LineElement`] properties (`startX`, `startY`, etc.)
    9. Draw the [`RootElement`] to a [`HtmlKanvas`] (which bridges to an HTML [Canvas])

    ```kotlin
    {% include kotlin/tutorial/Line2.kt %}
    ```

    This will render as:

    <canvas id="canvas2" width="100" height="100" style="outline: black 1px solid;"></canvas>
    <script>sample.tutorial.setupLine2(document.getElementById("canvas2"));</script>

    ### Bar Chart

    We can use a larger dataset with the [`LineElement`] to create a simple bar chart. We start by
    creating a larger HTML [Canvas]:

    ```html
    <canvas id="barchart" width="250" height="100" style="outline: black 1px solid;"></canvas>
    ```

    Then we can construct the bar chart in [Kotlin]™:

    1. Extract the `width` and `height` of the HTML [Canvas] as `Float`s
    2. Create an example dataset (1 through 10, incrementing by 1)
    3. Configure scaling on the x-axis (essentially, mapping dataset indices to the width of the [Canvas])
    4. Configure scaling on the y-axis (mapping the range of dataset values to the height of the [Canvas])<sup>1</sup>
    5. Configure the color and width of the bars
    6. Create a [`RootElement`]
    7. Access [`RootElement`] as a [`Selection`]
    8. Selects all [`LineElement`]s that are children of the [`RootElement`]
    9. Associate data with the [`Selection`]
    10. Combine each element of the data with a [`LineElement`]
    11. Iterate over each item of the data (indexed)
    12. Assign scaled values to the [`LineElement`] properties (`startX`, `startY`, etc.)
    13. Assign the bar paint (from step 5)
    14. Draw the [`RootElement`] to a [`HtmlKanvas`] (which bridges to an HTML [Canvas])

    <sup>1</sup> Note that for step 4 the order of the `range` values are flipped (with `height` listed
    before `0f`). This is to invert y-axis rendering, since the rendering origin is the upper left
    corner (with y increasing downward).

    ```kotlin
    {% include kotlin/tutorial/BarChart.kt %}
    ```

    This will render as:

    <canvas id="barchart" width="250" height="100" style="outline: black 1px solid;"></canvas>
    <script>sample.tutorial.setupBarChart(document.getElementById("barchart"));</script>

    [Canvas]: https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API
    [D3]: https://d3js.org/
    [DOM]: https://en.wikipedia.org/wiki/Document_Object_Model
    [Document Object Model]: https://en.wikipedia.org/wiki/Document_Object_Model
    [Kotlin]: https://kotlinlang.org/
    [`Element`]: https://juullabs.github.io/krayon/api/element/com.juul.krayon.element/-element/index.html
    [`HtmlKanvas`]: https://juullabs.github.io/krayon/api/kanvas/com.juul.krayon.kanvas/-html-kanvas/index.html
    [`LineElement`]: https://juullabs.github.io/krayon/api/element/com.juul.krayon.element/-line-element/index.html
    [`RootElement`]: https://juullabs.github.io/krayon/api/element/com.juul.krayon.element/-root-element/index.html
    [`Selection`]: https://juullabs.github.io/krayon/api/selection/com.juul.krayon.selection/-selection/index.html
    [multiplatform]: https://kotlinlang.org/docs/multiplatform.html
""".trimIndent()
