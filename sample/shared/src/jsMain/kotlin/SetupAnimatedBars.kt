import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.element.view.attachAdapter
import com.juul.krayon.sample.data.randomBars
import com.juul.krayon.sample.updaters.animatedBarChart
import org.w3c.dom.HTMLCanvasElement

@JsExport
fun setupAnimatedBars(element: HTMLCanvasElement) {
    element.attachAdapter(
        ElementViewAdapter(
            dataSource = randomBars(),
            updater = ::animatedBarChart,
        ),
    )
}
