package com.juul.krayon.documentation.features.samples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.components.CodeView
import com.juul.krayon.documentation.samples.MovingSineWaveView

@Composable
fun SineWaveScreen() {
    Column {
        MovingSineWaveView(Modifier.fillMaxWidth().height(300.dp))

        Text("Data")
        CodeView("data/SineWave.kt")
        Text("Updater")
        CodeView("updaters/LineChart.kt")
        Text("Glue")
        CodeView("MovingSineWaveView.kt")
    }
}

