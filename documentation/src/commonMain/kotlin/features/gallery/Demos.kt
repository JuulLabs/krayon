package com.juul.krayon.documentation.features.gallery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.components.ValueSlider
import com.juul.krayon.documentation.samples.DonutChart
import com.juul.krayon.documentation.samples.TreemapChart
import com.juul.krayon.documentation.samples.donutChart
import com.juul.krayon.documentation.samples.gaugeChart
import com.juul.krayon.documentation.samples.interactiveTreemap
import com.juul.krayon.documentation.samples.movingSineWave
import com.juul.krayon.documentation.samples.packageSizes
import com.juul.krayon.documentation.samples.sineWaveChart
import com.juul.krayon.documentation.samples.treemapChart
import com.juul.krayon.hierarchy.treemap.SliceAndDice
import com.juul.krayon.hierarchy.treemap.Squarify

@Composable
fun SineWaveDemo(modifier: Modifier = Modifier) {
    val data = remember { movingSineWave() }
    ElementView(data, ::sineWaveChart, modifier)
}

@Composable
fun InteractiveTreemapDemo(modifier: Modifier = Modifier) {
    val (flow, updater) = remember { interactiveTreemap() }
    ElementView(flow, updater, modifier)
}

@Composable
fun DonutDemo(modifier: Modifier = Modifier, withControls: Boolean = false) {
    val startAngle = remember { mutableStateOf(0f) }
    val endAngle = remember { mutableStateOf(6.2832f) }
    val cornerRadius = remember { mutableStateOf(4f) }
    val padAngle = remember { mutableStateOf(0.02f) }
    val innerRadius = remember { mutableStateOf(0.5f) }
    val data = remember {
        derivedStateOf {
            DonutChart(
                startAngle = startAngle.value,
                endAngle = endAngle.value,
                cornerRadius = cornerRadius.value,
                padAngle = padAngle.value,
                innerRadiusFraction = innerRadius.value,
            )
        }
    }
    Column(modifier) {
        ElementView(data, ::donutChart, Modifier.fillMaxWidth().weight(1f))
        if (withControls) {
            ValueSlider("Start angle", 0f, endAngle.value, startAngle)
            ValueSlider("End angle", startAngle.value, 6.2832f, endAngle)
            ValueSlider("Corner radius", 0f, 32f, cornerRadius)
            ValueSlider("Pad angle", 0f, 0.1f, padAngle)
            ValueSlider("Inner radius", 0f, 0.95f, innerRadius)
        }
    }
}

@Composable
fun GaugeDemo(modifier: Modifier = Modifier, withControls: Boolean = false) {
    val fraction = remember { mutableStateOf(0.72f) }
    Column(modifier) {
        ElementView(fraction, ::gaugeChart, Modifier.fillMaxWidth().weight(1f))
        if (withControls) {
            ValueSlider("Value", 0f, 1f, fraction)
        }
    }
}

@Composable
fun TreemapDemo(modifier: Modifier = Modifier, withControls: Boolean = false) {
    val squarify = remember { mutableStateOf(true) }
    val data = remember {
        derivedStateOf {
            TreemapChart(
                data = packageSizes,
                tileMethod = if (squarify.value) Squarify() else SliceAndDice,
            )
        }
    }
    Column(modifier) {
        ElementView(data, ::treemapChart, Modifier.fillMaxWidth().weight(1f))
        if (withControls) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tiling:", style = MaterialTheme.typography.body2)
                TileMethodOption("Squarify", selected = squarify.value) { squarify.value = true }
                TileMethodOption("Slice and dice", selected = !squarify.value) { squarify.value = false }
            }
        }
    }
}

@Composable
private fun TileMethodOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(label, style = MaterialTheme.typography.body2)
    }
}
