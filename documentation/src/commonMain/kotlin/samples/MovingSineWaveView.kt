package com.juul.krayon.documentation.samples

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.samples.data.movingSineWave
import com.juul.krayon.documentation.samples.updaters.lineChart

@Composable
fun MovingSineWaveView(
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colors
    ElementView(
        dataSource = remember { movingSineWave() },
        updateElements = { root, width, height, data ->
            lineChart(root, width, height, data, colors)
        },
        modifier,
    )
}
