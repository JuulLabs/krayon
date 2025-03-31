package com.juul.krayon.documentation.features.samples

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.samples.MovingSineWaveView
import com.juul.krayon.documentation.samples.interactiveTreeChart

@Composable
fun SamplesScreen(
    onSineWaveClick: () -> Unit,
    onTreeChartClick: () -> Unit,
) {
    Row(modifier = Modifier.height(300.dp)) {
        CodeCard(
            Modifier.weight(1f),
            onPlayClick = onSineWaveClick,
        ) {
            MovingSineWaveView(Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.size(8.dp))

        CodeCard(
            Modifier.weight(1f),
            onPlayClick = onTreeChartClick,
        ) {
            val (flow, update) = remember { interactiveTreeChart() }
            ElementView(
                dataSource = flow,
                updateElements = update,
                Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun CodeCard(
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Card(modifier) {
        Box(modifier = Modifier.padding(8.dp)) {
            content()
            FloatingActionButton(
                onClick = onPlayClick,
                modifier = Modifier.align(BottomEnd).padding(5.dp),
            ) {
                Icon(Icons.Filled.PlayArrow, "View")
            }
        }
    }
}
