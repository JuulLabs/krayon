import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.element.view.attachAdapter
import com.juul.krayon.sample.data.movingSineWave
import com.juul.krayon.sample.updaters.lineChart
import org.w3c.dom.HTMLCanvasElement

@JsExport
fun setupMovingSineWave(element: HTMLCanvasElement) {
    element.attachAdapter(
        ElementViewAdapter(
            dataSource = movingSineWave(),
            updater = ::lineChart,
        ),
    )
}
