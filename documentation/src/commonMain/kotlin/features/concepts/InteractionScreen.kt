package com.juul.krayon.documentation.features.concepts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.features.gallery.InteractiveTreemapDemo
import com.juul.krayon.documentation.features.gallery.SineWaveDemo

@Composable
fun InteractionScreen() {
    Column {
        Text("Interaction & animation", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            Krayon has no transition engine or event system to learn. Both interaction and
            animation fall out of one property: **a chart is a function of its data**. New data in,
            updated chart out.

            ## Animation: data over time

            Feed `ElementView` a `Flow` and it re-renders on every emission, synchronized to the
            display's frame rate. A flow that emits continuously is an animation:

            ```kotlin
            fun movingSineWave(): Flow<List<Point>> = flow {
                var offset = 0f
                while (currentCoroutineContext().isActive) {
                    emit(sineWave(offset))
                    offset += step
                }
            }

            ElementView(movingSineWave(), ::sineWaveChart)
            ```
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DemoCard(height = 260.dp) {
            SineWaveDemo(Modifier.fillMaxSize())
        }
        MarkdownBlock(
            """
            Because the data join updates elements in place, only the values that changed get
            redrawn differently — the chart structure is stable across frames.

            ## Interaction: events as data

            Interactable elements (`RectangleElement`, `CircleElement`, `LineElement`,
            `PathElement`, ...) accept click and hover handlers:

            ```kotlin
            .each { (datum) ->
                onClick { selectionState.value = datum }
                onHoverChanged { _, hovered -> hoveredState.value = datum.takeIf { hovered } }
            }
            ```

            The idiomatic loop is: handlers write to state (`MutableStateFlow`), state feeds the
            data flow, and the chart re-renders with selection/hover baked into the data. Hover to
            highlight and click to select below — then read the code in the
            [gallery](#gallery/interactive-treemap):
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DemoCard(height = 320.dp) {
            InteractiveTreemapDemo(Modifier.fillMaxSize())
        }
        MarkdownBlock(
            """
            Hit-testing uses each element's actual geometry (`getInteractionPath()`), and the
            top-most element with a handler wins — occlusion works the way you'd expect. On touch
            screens, presses double as hover, so hover states remain useful as pre-selection
            feedback on mobile.

            ## Differences from D3

            There is no equivalent of `d3-transition`'s tweening (`.transition().duration(750)`) —
            drive value changes from your data source instead (e.g. animate with Compose's
            `animateFloatAsState` and feed the value into the chart data). Zoom and brush behaviors
            are also not yet available.
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}
