package com.juul.krayon.documentation.samples.tutorial

import com.juul.krayon.element.LineElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.HtmlKanvas
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.each
import org.w3c.dom.HTMLCanvasElement

@JsExport
fun setupLine1(element: HTMLCanvasElement) {
    val root = RootElement()       // 1
    root.asSelection()             // 2
        .append(LineElement)       // 3
        .each {
            startX = 10f           // 4
            startY = 10f           // 4
            endX = 90f             // 4
            endY = 90f             // 4
        }
    root.draw(HtmlKanvas(element)) // 5
}
