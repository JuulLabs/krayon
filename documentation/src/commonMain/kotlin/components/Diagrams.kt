package com.juul.krayon.documentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.juul.krayon.color.Color
import com.juul.krayon.color.black
import com.juul.krayon.color.toColor
import com.juul.krayon.color.white
import com.juul.krayon.compose.ElementView
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

// Mermaid-inspired palette, matching the diagrams these replaced.
private val nodeFill = "#ececff".toColor()
private val nodeStroke = "#9370db".toColor()
private val krayonFill = "#b3e5fc".toColor()
private val webFill = "#e8d44d".toColor()
private val androidFill = "#9fc137".toColor()
private val iosFill = "#adadad".toColor()

private val diagramFont = Font(sansSerif)
private val nodeLabelPaint = Paint.Text(black, 13f, Paint.Text.Alignment.Center, diagramFont)
private val groupLabelPaint = Paint.Text(black, 12f, Paint.Text.Alignment.Center, diagramFont)
private val edgePaint = Paint.Stroke(black.copy(alpha = 0xAA), 1.5f)
private val arrowheadPaint = Paint.Fill(black.copy(alpha = 0xAA))

/** A labeled, rounded box of the diagram. Group boxes draw their label along the top edge. */
private data class DiagramBox(
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
private data class DiagramEdge(
    val fromX: Float,
    val fromY: Float,
    val toX: Float,
    val toY: Float,
)

private fun node(label: String, left: Float, top: Float, right: Float, bottom: Float) =
    DiagramBox(label, left, top, right, bottom, nodeFill, nodeStroke)

private fun group(label: String, left: Float, top: Float, right: Float, bottom: Float, fill: Color) =
    DiagramBox(label, left, top, right, bottom, fill, black.copy(alpha = 0x66), isGroup = true)

private fun arrowheadPath(x: Float, y: Float): Path = Path {
    moveTo(x, y)
    lineTo(x - 8f, y - 4f)
    lineTo(x - 8f, y + 4f)
    close()
}

private fun drawDiagram(root: RootElement, boxes: List<DiagramBox>, edges: List<DiagramEdge>) {
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

/** `Data --D3--> DOM`: how D3 drives the browser's document object model. */
private fun dataToDomDiagram(root: RootElement, width: Float, height: Float, data: Unit) {
    val boxWidth = 110f
    val boxTop = height / 2f - 24f
    val boxBottom = height / 2f + 24f
    val gap = 130f
    val left = (width - (boxWidth * 2f + gap)) / 2f

    val dataBox = node("Data", left, boxTop, left + boxWidth, boxBottom)
    val domBox = node("DOM", left + boxWidth + gap, boxTop, left + boxWidth * 2f + gap, boxBottom)

    drawDiagram(
        root,
        boxes = listOf(dataBox, domBox),
        edges = listOf(DiagramEdge(dataBox.right, dataBox.centerY, domBox.left, domBox.centerY)),
    )

    root.asSelection()
        .selectAll(TextElement.withKind("edge-label"))
        .data(listOf(data))
        .join { append(TextElement).each { kind = "edge-label" } }
        .each {
            text = "D3"
            x = width / 2f
            y = height / 2f - 10f
            paint = nodeLabelPaint
        }
}

/** The Krayon architecture: data binds to elements, elements render through platform kanvases. */
private fun classStructureDiagram(root: RootElement, width: Float, height: Float, data: Unit) {
    val pad = 4f
    val nodeHeight = 34f
    val groupLabelHeight = 24f
    val groupPad = 8f
    val rowGap = 10f

    // Right column: SvgKanvas, then the Web/Android/iOS platform groups.
    val columnRight = width - pad - 16f
    val columnLeft = maxOf(columnRight - width * 0.3f, columnRight - 240f)
    var y = pad + 30f

    val svgNode = node("SvgKanvas", columnLeft, y, columnRight, y + nodeHeight)
    y = svgNode.bottom + rowGap

    val webGroup = group("Web", columnLeft - groupPad, y, columnRight + groupPad, y + groupLabelHeight + nodeHeight + groupPad, webFill)
    val htmlNode = node("HtmlKanvas", columnLeft, y + groupLabelHeight, columnRight, y + groupLabelHeight + nodeHeight)
    y = webGroup.bottom + rowGap

    val androidBottom = y + groupLabelHeight + nodeHeight * 2f + rowGap + groupPad
    val androidGroup = group("Android", columnLeft - groupPad, y, columnRight + groupPad, androidBottom, androidFill)
    val androidNode = node("AndroidKanvas", columnLeft, y + groupLabelHeight, columnRight, y + groupLabelHeight + nodeHeight)
    val composeNode = node(
        "ComposeKanvas",
        columnLeft,
        androidNode.bottom + rowGap,
        columnRight,
        androidNode.bottom + rowGap + nodeHeight,
    )
    y = androidGroup.bottom + rowGap

    val iosGroup = group("iOS", columnLeft - groupPad, y, columnRight + groupPad, y + groupLabelHeight + nodeHeight + groupPad, iosFill)
    val cgNode = node("CGContextKanvas", columnLeft, y + groupLabelHeight, columnRight, y + groupLabelHeight + nodeHeight)

    // The Krayon container wraps Elements and everything in the right column.
    val krayonGroup = group(
        "Krayon",
        width * 0.22f,
        pad,
        width - pad,
        iosGroup.bottom + pad + 4f,
        krayonFill,
    )
    val elementsNode = node(
        "Elements",
        krayonGroup.left + 24f,
        krayonGroup.centerY - nodeHeight / 2f,
        krayonGroup.left + 24f + minOf(width * 0.16f, 130f),
        krayonGroup.centerY + nodeHeight / 2f,
    )

    // Data feeds into the element tree from outside of Krayon.
    val dataNode = node("Data", pad, krayonGroup.centerY - nodeHeight / 2f, width * 0.14f, krayonGroup.centerY + nodeHeight / 2f)

    val targets = listOf(svgNode, htmlNode, androidNode, composeNode, cgNode)
    drawDiagram(
        root,
        // Groups first so that nodes draw on top of them.
        boxes = listOf(krayonGroup, webGroup, androidGroup, iosGroup, dataNode, elementsNode) + targets,
        edges = listOf(DiagramEdge(dataNode.right, dataNode.centerY, elementsNode.left, elementsNode.centerY)) +
            targets.map { target ->
                DiagramEdge(elementsNode.right, elementsNode.centerY, target.left, target.centerY)
            },
    )
}

/** White panel framing for diagrams, readable in both light and dark themes. */
@Composable
private fun DiagramPanel(
    caption: String,
    height: Dp,
    content: @Composable () -> Unit,
) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(8.dp))
                .background(androidx.compose.ui.graphics.Color.White)
                .padding(8.dp),
        ) {
            content()
        }
        Text(
            text = caption,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
fun DataToDomDiagram(modifier: Modifier = Modifier) {
    DiagramPanel(caption = "Rendered live by Krayon — not an image.", height = 120.dp) {
        ElementView({ }, ::dataToDomDiagram, modifier.fillMaxWidth().height(104.dp))
    }
}

@Composable
fun ClassStructureDiagram(modifier: Modifier = Modifier) {
    DiagramPanel(caption = "Rendered live by Krayon — not an image.", height = 392.dp) {
        ElementView({ }, ::classStructureDiagram, modifier.fillMaxWidth().height(376.dp))
    }
}
