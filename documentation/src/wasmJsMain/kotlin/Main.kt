package com.juul.krayon.documentation

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import com.juul.krayon.documentation.features.gallery.sampleById
import kotlinx.browser.window
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    ComposeViewport {
        App(
            onNavHostReady = { navController ->
                // Wait for the NavHost to set the navigation graph.
                navController.currentBackStackEntryFlow.first()
                // Restore the destination from the URL (e.g. a bookmarked `#gallery/bar-chart`)
                // before binding: the binding itself only syncs subsequent navigation.
                val initialRoute = window.location.hash.substringAfter('#', "")
                if (isKnownRoute(initialRoute) && initialRoute != Screen.Home.route) {
                    navController.navigate(initialRoute)
                }
                navController.bindToBrowserNavigation()
            },
        )
    }
}

private fun isKnownRoute(route: String): Boolean =
    Screen.fromRoute(route) != null || sampleById(route.substringAfter("gallery/", "")) != null
