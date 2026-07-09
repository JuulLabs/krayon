import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.element.view.attachAdapter
import com.juul.krayon.sample.interactiveTreeChart
import org.w3c.dom.HTMLCanvasElement

@JsExport
fun setupInteractiveChart(element: HTMLCanvasElement) {
    val (flow, update) = interactiveTreeChart()
    element.attachAdapter(ElementViewAdapter(flow, update))
}
