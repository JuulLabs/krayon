package com.juul.krayon.documentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.juul.krayon.documentation.components.Sidebar
import com.juul.krayon.documentation.components.TopBar
import com.juul.krayon.documentation.theme.AppTheme

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    onNavHostReady: suspend (NavController) -> Unit = {},
) {
    val systemDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDarkTheme) }
    AppTheme(darkTheme) {
        Scaffold(
            topBar = {
                TopBar(
                    darkTheme = darkTheme,
                    onDarkThemeChange = { darkTheme = it },
                    onTitleClick = { navController.navigateTo(Screen.Home) },
                )
            },
        ) { padding ->
            Row(Modifier.fillMaxSize().padding(padding)) {
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                Sidebar(
                    currentRoute = currentBackStackEntry?.destination?.route,
                    onNavigate = { screen -> navController.navigateTo(screen) },
                )
                // Keyed on the back stack entry so that each destination starts scrolled to the top.
                val scrollState = remember(currentBackStackEntry) { ScrollState(initial = 0) }
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
                ) {
                    // Documentation text (including rendered markdown) is selectable.
                    SelectionContainer {
                        Column(Modifier.widthIn(max = 920.dp).padding(horizontal = 24.dp, vertical = 24.dp)) {
                            AppNavHost(navController)
                        }
                    }
                }
            }
        }
        LaunchedEffect(navController) {
            onNavHostReady(navController)
        }
    }
}

private fun NavHostController.navigateTo(screen: Screen) {
    navigate(screen.route) {
        launchSingleTop = true
        popUpTo(Screen.Home.route)
    }
}
