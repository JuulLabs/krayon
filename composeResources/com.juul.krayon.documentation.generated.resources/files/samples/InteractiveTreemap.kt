package com.juul.krayon.documentation.samples

import com.juul.krayon.box.setShapeFrom
import com.juul.krayon.color.Color
import com.juul.krayon.color.crimson
import com.juul.krayon.color.darkBlue
import com.juul.krayon.color.forestGreen
import com.juul.krayon.color.lerp
import com.juul.krayon.color.white
import com.juul.krayon.element.RectangleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.UpdateElement
import com.juul.krayon.hierarchy.flatHierarchy
import com.juul.krayon.hierarchy.removeHierarchy
import com.juul.krayon.hierarchy.sum
import com.juul.krayon.hierarchy.treemap.Treemap
import com.juul.krayon.hierarchy.treemap.layoutWith
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.sansSerif
import com.juul.krayon.scale.max
import com.juul.krayon.scale.min
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlin.random.Random

enum class Letter {
    Alpha,
    Beta,
    Gamma,
    Delta,
    Epsilon,
    Zeta,
    Eta,
    Theta,
    Iota,
    Kappa,
}

data class InteractiveTreemap(
    val selection: Letter?,
    val hovered: Letter?,
    val counts: Map<Letter, Int>,
)

/**
 * Interaction happens by feeding UI events back into the data flow: click and hover handlers
 * mutate state flows, which produce new data, which re-renders the chart.
 */
fun interactiveTreemap(): Pair<Flow<InteractiveTreemap>, UpdateElement<InteractiveTreemap>> {
    val selectionState = MutableStateFlow(null as Letter?)
    val hoveredState = MutableStateFlow(null as Letter?)
    val letterSizes = Letter.entries.associateWith { Random.nextInt(50, 500) }
    val dataFlow = combine(selectionState, hoveredState) { selection, hovered ->
        InteractiveTreemap(selection, hovered, letterSizes)
    }
    val updater = UpdateElement<InteractiveTreemap> { root, width, height, data ->
        updateInteractiveTreemap(
            root, width, height, data,
            clickHandler = { letter -> selectionState.value = letter.takeUnless { it == selectionState.value } },
            hoverHandler = { letter, hovered ->
                if (hovered) {
                    hoveredState.value = letter
                } else if (hoveredState.value == letter) {
                    hoveredState.value = null
                }
            },
        )
    }
    return dataFlow to updater
}

private val textPaint = Paint.Text(white, 12f, Paint.Text.Alignment.Center, Font(sansSerif))

private fun updateInteractiveTreemap(
    root: RootElement,
    width: Float,
    height: Float,
    data: InteractiveTreemap,
    clickHandler: (Letter) -> Unit,
    hoverHandler: (Letter, Boolean) -> Unit,
) {
    val min = data.counts.values.min { it }
    val max = data.counts.values.max { it }

    fun colorFor(letter: Letter): Color {
        val value = data.counts.getOrElse(letter) { 0 }
        val baseColor = if (letter == data.selection) {
            forestGreen
        } else {
            lerp(crimson, darkBlue, (value - min).toFloat() / (max - min))
        }
        return if (letter == data.hovered) lerp(baseColor, white, 0.25f) else baseColor
    }

    val lettersToTiles = flatHierarchy(data.counts.entries)
        .sum { it?.value?.toFloat() ?: 0f }
        .layoutWith(Treemap(width, height))
        .removeHierarchy()
        .toList()

    root.asSelection()
        .selectAll(RectangleElement)
        .data(lettersToTiles)
        .join { append(RectangleElement) }
        .each { (datum) ->
            val (entry, tile) = datum
            setShapeFrom(tile)
            paint = Paint.FillAndStroke(
                Paint.Fill(colorFor(entry.key)),
                Paint.Stroke(white, 2f),
            )
            onClick { clickHandler(entry.key) }
            onHoverChanged { _, hovered -> hoverHandler(entry.key, hovered) }
        }

    root.asSelection()
        .selectAll(TextElement)
        .data(lettersToTiles)
        .join { append(TextElement) }
        .each { (datum) ->
            val (entry, tile) = datum
            x = tile.centerX
            y = tile.centerY + 4f
            text = entry.key.name
            paint = textPaint
        }
}
