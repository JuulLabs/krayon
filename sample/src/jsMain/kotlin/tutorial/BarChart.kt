package tutorial

import com.juul.krayon.color.blue
import com.juul.krayon.element.LineElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.HtmlKanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import org.w3c.dom.HTMLCanvasElement

@JsExport
fun setupBarChart(element: HTMLCanvasElement) {
    val (width, height) = element.size // 1
    val data = (1..10).toList()        // 2

    val x = scale()                    // 3
        .domain(0, data.count() - 1)   // 3
        .range(0f, width)              // 3

    val y = scale()                    // 4
        .domain(0, data.max())         // 4
        .range(height, 0f)             // 4

    val barPaint = Paint.Stroke(       // 5
        color = blue,                  // 5
        width = x.scale(1),            // 5
    )

    val root = RootElement()           // 6
    root.asSelection()                 // 7
        .selectAll(LineElement)        // 8
        .data(data)                    // 9
        .join(LineElement)             // 10
        .each { (data, index) ->       // 11
            startX = x.scale(index)    // 12
            startY = y.scale(0)        // 12
            endX = x.scale(index)      // 12
            endY = y.scale(data)       // 12
            paint = barPaint           // 13
        }
    root.draw(HtmlKanvas(element))     // 14
}

private val HTMLCanvasElement.size
    get() = width.toFloat() to height.toFloat()
