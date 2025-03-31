package com.juul.krayon.documentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.juul.krayon.documentation.components.AppBar
import com.juul.krayon.documentation.theme.AppTheme

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    AppTheme {
        Scaffold(
            topBar = { AppBar(navController) },
        ) {
            Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.width(1000.dp).verticalScroll(rememberScrollState())) {
                    AppNavHost(navController)
                }
            }
        }
    }
}
