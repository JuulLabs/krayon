
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import com.juul.krayon.compose.ElementView
import com.juul.krayon.sample.PieChart
import com.juul.krayon.sample.interactiveTreeChart
import com.juul.krayon.sample.lineChart
import com.juul.krayon.sample.movingSineWave
import com.juul.krayon.sample.pieChart
import kotlin.math.PI

fun main() = singleWindowApplication {
    MaterialTheme {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            LineChart()
            PieChart()
            TouchChart()
        }
    }
}

@Composable
private fun LineChart() {
    ElementView(
        remember { movingSineWave() },
        ::lineChart,
        Modifier.defaultMinSize(360.dp, 360.dp).fillMaxWidth()
    )
}

@Composable
private fun PieChart() {
    Row {
        val arcs = listOf(1f, 2f, 3f, 4f, 5f, 6f)
        val startAngle = remember { mutableStateOf(0f) }
        val endAngle = remember { mutableStateOf((PI * 2).toFloat()) }
        val cornerRadius = remember { mutableStateOf(0f) }
        val paddingAngle = remember { mutableStateOf(0f) }
        val innerRadius = remember { mutableStateOf(0f) }

        // Convert state into a flow for consumption by the element view adapter.
        val charts = derivedStateOf {
            PieChart(arcs, startAngle.value, endAngle.value, cornerRadius.value, paddingAngle.value, innerRadius.value)
        }

        ElementView(
            charts,
            ::pieChart,
            Modifier.size(360.dp, 360.dp)
        )
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                ValueSlider("Start Angle", 0f, endAngle.value, startAngle)
                ValueSlider("End Angle", startAngle.value, 2 * PI.toFloat(), endAngle)
                ValueSlider("Corner Radius", 0f, 32f, cornerRadius)
                ValueSlider("Padding Angle", 0f, 0.1f, paddingAngle)
                ValueSlider("Inner Radius", 0f, 172f, innerRadius)
            }
        }
    }
}

@Composable
private fun TouchChart() {
    val (flow, update) = remember { interactiveTreeChart() }
    ElementView(flow, update, Modifier.size(640.dp, 320.dp))
}

@Composable
private fun ValueSlider(
    label: String,
    min: Float,
    max: Float,
    state: MutableState<Float>,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.width(120.dp))
        Slider(state.value, valueRange = min..max, onValueChange = { state.value = it }, modifier = Modifier.fillMaxWidth())
    }
}
