package com.juul.krayon.documentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.juul.krayon.documentation.components.AppBar
import com.juul.krayon.documentation.features.samples.SamplesScreen
import com.juul.krayon.documentation.features.tutorial.TutorialScreen

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    AppTheme {
        Scaffold(
            topBar = { AppBar(navController) },
        ) {
            AppNavHost(navController)
        }
    }
}

@Composable
private fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController,
        startDestination = Screen.Tutorial.name,
        modifier = Modifier.padding(16.dp),
    ) {
        composable(route = Screen.Tutorial.name) {
            TutorialScreen()
        }

        composable(route = Screen.Samples.name) {
            SamplesScreen()
        }
    }
}

