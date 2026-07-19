package com.juul.krayon.documentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.savedstate.read
import com.juul.krayon.documentation.features.concepts.AxesScreen
import com.juul.krayon.documentation.features.concepts.ColorScreen
import com.juul.krayon.documentation.features.concepts.HierarchyScreen
import com.juul.krayon.documentation.features.concepts.InteractionScreen
import com.juul.krayon.documentation.features.concepts.RenderingScreen
import com.juul.krayon.documentation.features.concepts.ScalesScreen
import com.juul.krayon.documentation.features.concepts.SelectionsScreen
import com.juul.krayon.documentation.features.concepts.ShapesScreen
import com.juul.krayon.documentation.features.d3.D3Screen
import com.juul.krayon.documentation.features.gallery.GalleryScreen
import com.juul.krayon.documentation.features.gallery.SampleDetailScreen
import com.juul.krayon.documentation.features.gallery.sampleById
import com.juul.krayon.documentation.features.gettingstarted.GettingStartedScreen
import com.juul.krayon.documentation.features.home.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigate = { screen -> navController.navigate(screen.route) })
        }
        composable(Screen.GettingStarted.route) { GettingStartedScreen() }
        composable(Screen.Selections.route) { SelectionsScreen() }
        composable(Screen.Scales.route) { ScalesScreen() }
        composable(Screen.Shapes.route) { ShapesScreen() }
        composable(Screen.Axes.route) { AxesScreen() }
        composable(Screen.Hierarchy.route) { HierarchyScreen() }
        composable(Screen.ColorConcept.route) { ColorScreen() }
        composable(Screen.Rendering.route) { RenderingScreen() }
        composable(Screen.Interaction.route) { InteractionScreen() }
        composable(Screen.D3.route) { D3Screen() }
        composable(Screen.Gallery.route) {
            GalleryScreen(
                onSampleClick = { sample -> navController.navigate(sampleDetailRoute(sample.id)) },
            )
        }
        composable(SAMPLE_DETAIL_ROUTE) { backStackEntry ->
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
