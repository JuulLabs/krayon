package com.juul.krayon.documentation.features.samples

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.features.samples.data.movingSineWave
import com.juul.krayon.documentation.features.samples.updaters.lineChart

@Composable
fun SamplesScreen(
    viewModel: SamplesViewModel = viewModel { SamplesViewModel() },
) {
    Row {
        ElementView(
            dataSource = remember { movingSineWave() },
            updateElements = ::lineChart,
            Modifier
                .defaultMinSize(220.dp, 220.dp)
                .clickable(onClick = viewModel::onSineWaveClick),
        )

        val (flow, update) = remember { interactiveTreeChart() }
        ElementView(
            dataSource = flow,
            updateElements = update,
            Modifier
                .defaultMinSize(220.dp, 220.dp)
                .clickable(onClick = viewModel::onChartClick),
        )
    }
}
