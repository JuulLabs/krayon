package com.juul.krayon.documentation.samples

import com.juul.krayon.color.Color
import com.juul.krayon.color.black
import com.juul.krayon.color.toColor
import com.juul.krayon.element.LineElement
import com.juul.krayon.element.PathElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.RoundedRectangleElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.withKind
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.sansSerif
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll

// A tiny box-and-arrow diagram renderer, in the mermaid palette.
val nodeFill = "#ececff".toColor()
val nodeStroke = "#9370db".toColor()

private val diagramFont = Font(sansSerif)
val nodeLabelPaint = Paint.Text(black, 13f, Paint.Text.Alignment.Center, diagramFont)
private val groupLabelPaint = Paint.Text(black, 12f, Paint.Text.Alignment.Center, diagramFont)
private val edgePaint = Paint.Stroke(black.copy(alpha = 0xAA), 1.5f)
private val arrowheadPaint = Paint.Fill(black.copy(alpha = 0xAA))

/** A labeled, rounded box. Group boxes draw their label along the top edge instead of centered. */
data class DiagramBox(
    val label: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val fill: Color,
    val stroke: Color,
    val isGroup: Boolean = false,
) {
    val centerX: Float get() = (left + right) / 2f
    val centerY: Float get() = (top + bottom) / 2f
}

/** A directed edge between box edges, drawn with an arrowhead at ([toX], [toY]). */
data class DiagramEdge(
    val fromX: Float,
    val fromY: Float,
    val toX: Float,
    val toY: Float,
)

fun node(label: String, left: Float, top: Float, right: Float, bottom: Float) =
    DiagramBox(label, left, top, right, bottom, nodeFill, nodeStroke)

fun group(label: String, left: Float, top: Float, right: Float, bottom: Float, fill: Color) =
    DiagramBox(label, left, top, right, bottom, fill, black.copy(alpha = 0x66), isGroup = true)

private fun arrowheadPath(x: Float, y: Float): Path = Path {
    moveTo(x, y)
    lineTo(x - 8f, y - 4f)
    lineTo(x - 8f, y + 4f)
    close()
}

/** Draws [boxes] (in order, so later boxes overlap earlier ones) and arrow [edges]. */
fun drawDiagram(root: RootElement, boxes: List<DiagramBox>, edges: List<DiagramEdge>) {
    root.asSelection()
        .selectAll(RoundedRectangleElement)
        .data(boxes)
        .join(RoundedRectangleElement)
        .each { (box) ->
            left = box.left
            top = box.top
            right = box.right
            bottom = box.bottom
            topLeftRadius = 6f
            topRightRadius = 6f
            bottomLeftRadius = 6f
            bottomRightRadius = 6f
            paint = Paint.FillAndStroke(
                Paint.Fill(box.fill),
                Paint.Stroke(box.stroke, 1f),
            )
        }

    root.asSelection()
        .selectAll(TextElement.withKind("label"))
        .data(boxes)
        .join { append(TextElement).each { kind = "label" } }
        .each { (box) ->
            text = box.label
            x = box.centerX
            y = if (box.isGroup) box.top + 16f else box.centerY + 4f
            paint = if (box.isGroup) groupLabelPaint else nodeLabelPaint
        }

    root.asSelection()
        .selectAll(LineElement)
        .data(edges)
        .join(LineElement)
        .each { (edge) ->
            startX = edge.fromX
            startY = edge.fromY
            endX = edge.toX - 6f
            endY = edge.toY
            paint = edgePaint
        }

    root.asSelection()
        .selectAll(PathElement.withKind("arrowhead"))
        .data(edges)
        .join { append(PathElement).each { kind = "arrowhead" } }
        .each { (edge) ->
            path = arrowheadPath(edge.toX, edge.toY)
            paint = arrowheadPaint
        }
}
