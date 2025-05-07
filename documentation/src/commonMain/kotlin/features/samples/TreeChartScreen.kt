package com.juul.krayon.documentation.features.samples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juul.krayon.documentation.components.Loading
import com.juul.krayon.documentation.highlight
import com.juul.krayon.documentation.theme.AppTheme

@Composable
fun TreeChartScreen(
    viewModel: TreeChartViewModel = viewModel { TreeChartViewModel() },
) {
    val code = viewModel.code.collectAsState().value
    if (code == null) {
        Loading()
    } else {
        val syntaxTheme = AppTheme.highlight
        val text = remember(syntaxTheme) { code.highlight(syntaxTheme) }
        Column {
            InteractiveTreeChartView(Modifier.fillMaxWidth().height(300.dp))
            Text(text)
        }
    }
}
