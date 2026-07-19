package com.juul.krayon.documentation.features.concepts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.color.steelBlue
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll

/** The classic D3 "data join" demo: one circle per datum, sized by value. */
private fun circleRow(root: RootElement, width: Float, height: Float, data: List<Int>) {
    root.asSelection()
        .selectAll(CircleElement)
        .data(data)
        .join(CircleElement)
        .each { (value, index) ->
            centerX = width / (data.size + 1) * (index + 1)
            centerY = height / 2f
            radius = 6f + value * 2f
            paint = Paint.Fill(steelBlue)
        }
}

@Composable
fun SelectionsScreen() {
    Column {
        Text("Selections", style = MaterialTheme.typography.h3)
        MarkdownBlock(
            """
            *The Krayon equivalent of [d3-selection](https://d3js.org/d3-selection).*

            Selections are how data drives the element tree. A `Selection` wraps a group of
            elements; operators on it select children, bind data, and update attributes.

            ## Selecting elements

            Every chart starts by wrapping an element as a selection, then selecting the elements
            to bind data to. Element companions double as selectors:

            ```kotlin
            root.asSelection()              // Selection of the RootElement
                .selectAll(CircleElement)   // ...its CircleElement descendants
            ```

            `select` picks the first match; `selectAll` picks every match. Both search all
            descendants, recursively.

            ## Binding data: the join

            `data` associates a list of data with the selected elements, and `join` reconciles the
            two — creating elements for surplus data (*enter*), keeping element+datum pairs
            (*update*), and removing surplus elements (*exit*):

            ```kotlin
            root.asSelection()
                .selectAll(CircleElement)
                .data(listOf(3, 1, 4, 1, 5))
                .join(CircleElement)          // append entering circles, remove exiting ones
                .each { (value, index) ->     // runs for every remaining element
                    centerX = 40f * (index + 1)
                    radius = 6f + value * 2f
                }
            ```

            Inside `each`, the receiver is the element and the argument destructures to
            `(datum, index, data)`. Try the join for yourself — the chart below re-runs the exact
            code above every time the data changes:
            """,
            Modifier.padding(vertical = 8.dp),
        )
        DataJoinDemo()
        MarkdownBlock(
            """
            ## Finer-grained joins

            `join` accepts blocks for each phase when you need different behavior for entering,
            updating, or exiting elements — the equivalent of D3's `enter`/`exit` handling. Set
            one-time attributes on enter, and per-datum attributes in `each`:

            ```kotlin
            .join {
                append(CircleElement).each {
                    radius = 3f                  // set once, when the element is created
                    paint = circlePaint
                }
            }
            .each { (point) ->
                centerX = x.scale(point.x)       // set on every update
                centerY = y.scale(point.y)
            }
            ```

            ## Kinds: tagging elements

            Charts often need several joins over the same element type — say, one `PathElement` for
            an area fill and another for its outline, or your own elements alongside the ones an
            axis creates internally. The `kind` attribute tags elements so selectors can tell them
            apart:

            ```kotlin
            body.selectAll(PathElement.withKind("line"))
                .data(listOf(data))
                .join { append(PathElement).each { kind = "line" } }
            ```

            Without the kind, `selectAll(PathElement)` would also match the `PathElement`s that
            `axisBottom` created inside the same subtree — remember that selections search *all*
            descendants.

            ## The full operator set

            | Krayon | D3 | Purpose |
            |---|---|---|
            | `asSelection()` | `d3.select(node)` | wrap an element |
            | `select(...)` / `selectAll(...)` | `selection.select` / `selectAll` | narrow the selection |
            | `data(...)` / `keyedData(...)` | `selection.data(data, key)` | bind data (optionally keyed) |
            | `join(...)` | `selection.join` | reconcile enter/update/exit |
            | `append(...)` / `insert(...)` | `selection.append` / `insert` | add child elements |
            | `each { }` | `selection.each` | mutate every element |
            | `merge(...)` | `selection.merge` | combine selections |
            | `filter { }` | `selection.filter` | subset a selection |
            | `order()` | `selection.order` | match document order to data order |
            | `remove()` | `selection.remove` | delete elements |
            """,
            Modifier.padding(vertical = 8.dp),
        )
    }
}

@Composable
private fun DataJoinDemo() {
    val data = remember { mutableStateOf(listOf(3, 1, 4, 1, 5)) }
    DemoCard(height = 200.dp, caption = "The circles are joined to ${data.value} — add or remove data to see enter and exit in action.") {
        Column(Modifier.fillMaxSize()) {
            ElementView(data, ::circleRow, Modifier.fillMaxWidth().weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { data.value += (data.value.size * 7 + 3) % 9 }) {
                    Text("Add datum")
                }
                OutlinedButton(
                    onClick = { data.value = data.value.dropLast(1) },
                    enabled = data.value.size > 1,
                ) {
                    Text("Remove datum")
                }
            }
        }
    }
}
