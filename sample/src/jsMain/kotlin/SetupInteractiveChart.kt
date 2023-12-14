import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.element.view.attachAdapter
import com.juul.krayon.sample.interactiveTreeChart
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

@JsExport
fun setupInteractiveChart(elementId: String) {
    val canvasElement = document.getElementById(elementId) as HTMLCanvasElement
    val (flow, update) = interactiveTreeChart()
    canvasElement.attachAdapter(ElementViewAdapter(flow, update))
}
