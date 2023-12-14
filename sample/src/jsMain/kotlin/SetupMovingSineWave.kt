import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.element.view.attachAdapter
import com.juul.krayon.sample.data.movingSineWave
import com.juul.krayon.sample.updaters.lineChart
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

@JsExport
fun setupMovingSineWave(elementId: String) {
    (document.getElementById(elementId) as HTMLCanvasElement)
        .attachAdapter(
            ElementViewAdapter(
                dataSource = movingSineWave(),
                updater = ::lineChart,
            ),
        )
}
