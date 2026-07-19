package com.juul.krayon.documentation.samples

import com.juul.krayon.color.toColor
import com.juul.krayon.element.RootElement

private val krayonFill = "#b3e5fc".toColor()
private val webFill = "#e8d44d".toColor()
private val androidFill = "#9fc137".toColor()
private val iosFill = "#adadad".toColor()

/** The Krayon architecture: data binds to elements, elements render through platform kanvases. */
fun classStructureDiagram(root: RootElement, width: Float, height: Float, data: Unit) {
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
