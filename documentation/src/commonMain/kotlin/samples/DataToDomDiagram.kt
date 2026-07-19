package com.juul.krayon.documentation.samples

import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.withKind
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll

/** `Data --D3--> DOM`: how D3 drives the browser's document object model. */
fun dataToDomDiagram(root: RootElement, width: Float, height: Float, data: Unit) {
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

    // Label the arrow, like mermaid's `Data -- D3 --> DOM`.
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
