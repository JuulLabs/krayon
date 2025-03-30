package com.juul.krayon.documentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.juul.krayon.documentation.Screen

@Composable
fun AppBar(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    TopAppBar(
        title = {},
        actions = {
            Actions {
                navController.navigate(it) {
                    launchSingleTop = true
                    currentBackStackEntry?.destination?.route?.apply {
                        popUpTo(this) { inclusive = true }
                    }
                }
            }
        },
    )
}

@Composable
private fun Actions(
    onNavigateToRoute: (route: String) -> Unit,
) {
    Text(
        text = "Tutorial",
        modifier = Modifier.clickable {
            onNavigateToRoute(Screen.Tutorial.name)
        }.padding(10.dp),
    )

    Text(
        text = "Samples",
        modifier = Modifier.clickable {
            onNavigateToRoute(Screen.Samples.name)
        }.padding(10.dp),
    )
}
