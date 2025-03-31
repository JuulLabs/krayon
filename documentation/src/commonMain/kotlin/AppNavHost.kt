package com.juul.krayon.documentation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.juul.krayon.documentation.features.samples.SamplesScreen
import com.juul.krayon.documentation.features.tutorial.TutorialScreen
import com.juul.krayon.documentation.features.samples.SineWaveScreen
import com.juul.krayon.documentation.features.samples.TreeChartScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController,
        startDestination = Screen.Tutorial.name,
        modifier = Modifier.padding(16.dp),
    ) {
        composable(route = Screen.Tutorial.name) {
            TutorialScreen()
        }

        composable(route = Screen.Samples.name) {
            SamplesScreen(
                onSineWaveClick = { navController.navigate(Screen.SineWaveSample.name) },
                onTreeChartClick = { navController.navigate(Screen.TreeChartSample.name) }
            )
        }

        composable(route = Screen.SineWaveSample.name) {
            SineWaveScreen()
        }

        composable(route = Screen.TreeChartSample.name) {
            TreeChartScreen()
        }
    }
}
