package com.juul.krayon.documentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.savedstate.read
import com.juul.krayon.documentation.features.gallery.GalleryScreen
import com.juul.krayon.documentation.features.gallery.SampleDetailScreen
import com.juul.krayon.documentation.features.gallery.sampleById

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        Screen.entries.forEach { screen ->
            composable(route = screen.route) {
                when (screen) {
                    Screen.Gallery -> GalleryScreen(
                        onSampleClick = { sample -> navController.navigate(sampleDetailRoute(sample.id)) },
                    )
                    else -> Text(screen.title)
                }
            }
        }
        composable(route = SAMPLE_DETAIL_ROUTE) { backStackEntry ->
            val sampleId = backStackEntry.arguments?.read { getStringOrNull("sampleId") }
            val sample = sampleById(sampleId)
            if (sample != null) {
                SampleDetailScreen(sample)
            } else {
                Text("Unknown sample: $sampleId")
            }
        }
    }
}
