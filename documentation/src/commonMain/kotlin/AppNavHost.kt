package com.juul.krayon.documentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.savedstate.read

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        Screen.entries.forEach { screen ->
            composable(route = screen.route) {
                Text(screen.title)
            }
        }
        composable(route = SAMPLE_DETAIL_ROUTE) { backStackEntry ->
            val sampleId = backStackEntry.arguments?.read { getStringOrNull("sampleId") }
            Text(sampleId.orEmpty())
        }
    }
}
