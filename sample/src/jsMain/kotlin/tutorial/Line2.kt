package tutorial

import com.juul.krayon.element.LineElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.HtmlKanvas
import com.juul.krayon.sample.Point
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import org.w3c.dom.HTMLCanvasElement

@JsExport
fun setupLine2(element: HTMLCanvasElement) {
    val root = RootElement()                              // 1
    root.asSelection()                                    // 2
        .selectAll(LineElement)                           // 3
        .data(listOf(Point(10f, 90f) to Point(90f, 10f))) // 4
        .join(LineElement)                                // 5
        .each { (data) ->                                 // 6
            val (start, end) = data                       // 7
            startX = start.x                              // 8
            startY = start.y                              // 8
            endX = end.x                                  // 8
            endY = end.y                                  // 8
        }
    root.draw(HtmlKanvas(element))                        // 9
}
