package com.juul.krayon.documentation.samples

import com.juul.krayon.box.setShapeFrom
import com.juul.krayon.color.Color
import com.juul.krayon.color.crimson
import com.juul.krayon.color.darkBlue
import com.juul.krayon.color.forestGreen
import com.juul.krayon.color.lerp
import com.juul.krayon.color.white
import com.juul.krayon.element.ClickHandler
import com.juul.krayon.element.HoverHandler
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
import com.juul.krayon.kanvas.Paint.Fill
import com.juul.krayon.kanvas.Paint.Text
import com.juul.krayon.kanvas.Paint.Text.Alignment.Center
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

data class InteractiveTreeChart(
    val selection: Letter?,
    val hovered: Letter?,
    val counts: Map<Letter, Int>,
)

internal fun interactiveTreeChart(): Pair<Flow<InteractiveTreeChart>, UpdateElement<InteractiveTreeChart>> {
    val selectionState = MutableStateFlow(null as Letter?)
    val hoveredState = MutableStateFlow(null as Letter?)
    val letterSizes = Letter.entries.associateWith { Random.nextInt(50, 500) }
    val dataFlow = combine(selectionState, hoveredState) { selection, hovered ->
        InteractiveTreeChart(selection, hovered, letterSizes)
    }
    val updater = UpdateElement<InteractiveTreeChart> { root, width, height, data ->
        updateInteractiveTreeChart(
            root,
            width,
            height,
            data,
            clickHandler = { letter -> selectionState.value = letter.takeUnless { it == selectionState.value } },
            hoverHandler = { letter, hovered ->
                if (!hovered && hoveredState.value == letter) {
                    hoveredState.value = null
                } else if (hovered) {
                    hoveredState.value = letter
                }
            },
        )
    }
    return dataFlow to updater
}

private fun updateInteractiveTreeChart(
    root: RootElement,
    width: Float,
    height: Float,
    data: InteractiveTreeChart,
    clickHandler: ClickHandler<Letter>,
    hoverHandler: HoverHandler<Letter>,
) {
    val min = data.counts.values.min { it }
    val max = data.counts.values.max { it }

    fun colorFor(letter: Letter): Color {
        val value = data.counts[letter] ?: 0
        val baseColor = if (letter == data.selection) {
            forestGreen
        } else {
            lerp(crimson, darkBlue, (value - min).toFloat() / (max - min))
        }
        return if (letter == data.hovered) {
            lerp(baseColor, white, 0.25f)
        } else {
            baseColor
        }
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
        .each { (d) ->
            val (entry, tile) = d
            setShapeFrom(tile)
            paint = Paint.FillAndStroke(
                Fill(colorFor(entry.key)),
                Paint.Stroke(white, 2f),
            )
            onClick { clickHandler.onClick(entry.key) }
            onHoverChanged { _, hovered -> hoverHandler.onHoverChanged(entry.key, hovered) }
        }

    val textPaint = Text(white, size = 12f, alignment = Center, Font(sansSerif))
    root.asSelection()
        .selectAll(TextElement)
        .data(lettersToTiles)
        .join { append(TextElement) }
        .each { (d) ->
            val (entry, tile) = d
            x = tile.centerX
            y = tile.centerY + textPaint.size * 0.75f
            text = entry.key.name
            paint = textPaint
        }
}
