package com.juul.krayon.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.sample.databinding.ActivityPieBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlin.math.PI

private val arcs = listOf(1f, 2f, 3f, 4f, 5f, 6f)

class PieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityPieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startAngle = MutableStateFlow(0f)
        val endAngle = MutableStateFlow((PI * 2).toFloat())
        val cornerRadius = MutableStateFlow(0f)
        val paddingAngle = MutableStateFlow(0f)
        val innerRadius = MutableStateFlow(0f)

        binding.sliderStart.configure(min = 0f, max = PI.toFloat(), state = startAngle)
        binding.sliderEnd.configure(min = PI.toFloat(), max = (2 * PI).toFloat(), state = endAngle)
        binding.sliderRadius.configure(min = 0f, max = 32f, state = cornerRadius)
        binding.sliderPad.configure(min = 0f, max = 0.1f, state = paddingAngle)
        val displayWidthDp = with(resources.displayMetrics) { (widthPixels - binding.root.paddingLeft - binding.root.paddingRight) / density }
        binding.sliderInner.configure(min = 0f, max = 0.4f * displayWidthDp, state = innerRadius)

        // Convert state into a flow for consumption by the element view adapter.
        val charts = combine(startAngle, endAngle, cornerRadius, paddingAngle, innerRadius) { startAngle, endAngle, cornerRadius, paddingAngle, innerRadius ->
            PieChart(arcs, startAngle, endAngle, cornerRadius, paddingAngle, innerRadius)
        }

        binding.sineView.adapter = ElementViewAdapter(
            dataSource = charts,
            updater = ::pieChart
        )
    }

    private fun Slider.configure(min: Float, max: Float, state: MutableStateFlow<Float>) {
        valueFrom = min
        valueTo = max
        value = state.value
        addOnChangeListener { _, value, _ -> state.value = value }
    }
}
