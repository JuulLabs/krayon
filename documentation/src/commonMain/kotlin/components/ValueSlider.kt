package com.juul.krayon.documentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ValueSlider(
    label: String,
    min: Float,
    max: Float,
    state: MutableState<Float>,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.width(140.dp), style = MaterialTheme.typography.body2)
        Slider(
            value = state.value,
            valueRange = min..max,
            onValueChange = { state.value = it },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
