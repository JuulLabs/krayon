package com.juul.krayon.documentation.features.samples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.components.CodeView

@Composable
fun TreeChartScreen() {
    Column {
        InteractiveTreeChartView(Modifier.fillMaxWidth().height(300.dp))
        CodeView("InteractiveTreeChart.kt")
    }
}
