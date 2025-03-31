package com.juul.krayon.documentation.features.samples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.samples.interactiveTreeChart

@Composable
fun InteractiveTreeChartView(
    modifier: Modifier = Modifier,
) {
    val (flow, update) = remember { interactiveTreeChart() }
    ElementView(dataSource = flow, updateElements = update, modifier)
}
