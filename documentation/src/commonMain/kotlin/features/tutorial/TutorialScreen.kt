package com.juul.krayon.documentation.features.tutorial

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TutorialScreen(
    viewModel: TutorialViewModel = viewModel { TutorialViewModel() },
) {
    val count by viewModel.count.collectAsState(0)
    Column {
        Text("Tutorial screen")
        Text(count.toString())
    }
}
