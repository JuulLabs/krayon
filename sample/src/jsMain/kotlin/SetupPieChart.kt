import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.element.view.attachAdapter
import com.juul.krayon.sample.PieChart
import com.juul.krayon.sample.pieChart
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLInputElement
import kotlin.math.PI

private val arcs = listOf(1f, 2f, 3f, 4f, 5f, 6f)

@JsExport
fun setupPieChart(element: HTMLCanvasElement) {
    fun configure(id: String, min: Float, max: Float, state: MutableStateFlow<Float>) {
        val input = document.getElementById(id) as HTMLInputElement
        input.min = min.toString()
        input.max = max.toString()
        input.step = ((max - min) / 1000f).toString()
        input.value = state.value.toString()
        input.oninput = { _ ->
            state.value = input.value.toFloatOrNull() ?: 0f
            Unit // Apparently assignment doesn't return a `dynamic`, which the function interface requires.
        }
    }

    val startAngle = MutableStateFlow(0f)
    val endAngle = MutableStateFlow((PI * 2).toFloat())
    val cornerRadius = MutableStateFlow(0f)
    val paddingAngle = MutableStateFlow(0f)
    val innerRadius = MutableStateFlow(0f)

    configure("pie-start-angle", min = 0f, max = PI.toFloat(), state = startAngle)
    configure("pie-end-angle", min = PI.toFloat(), max = (2 * PI).toFloat(), state = endAngle)
    configure("pie-corner-radius", min = 0f, max = 32f, state = cornerRadius)
    configure("pie-padding-angle", min = 0f, max = 0.1f, state = paddingAngle)
    configure("pie-inner-radius", min = 0f, max = 192f, state = innerRadius)

    // Convert state into a flow for consumption by the element view adapter.
    val charts =
        combine(
            startAngle,
            endAngle,
            cornerRadius,
            paddingAngle,
            innerRadius,
        ) { startAngle, endAngle, cornerRadius, paddingAngle, innerRadius ->
            PieChart(arcs, startAngle, endAngle, cornerRadius, paddingAngle, innerRadius)
        }

    element.attachAdapter(
        ElementViewAdapter(
            dataSource = charts,
            updater = ::pieChart,
        ),
    )
}
