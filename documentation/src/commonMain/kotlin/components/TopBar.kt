package com.juul.krayon.documentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

const val GITHUB_URL = "https://github.com/JuulLabs/krayon"
const val API_URL = "https://juullabs.github.io/krayon/api/"

@Composable
fun TopBar(onTitleClick: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    TopAppBar(
        title = {
            Text(
                text = "Krayon",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onTitleClick),
            )
        },
        actions = {
            TopBarLink("API") { uriHandler.openUri(API_URL) }
            TopBarLink("GitHub") { uriHandler.openUri(GITHUB_URL) }
        },
        backgroundColor = MaterialTheme.colors.primary,
    )
}

@Composable
private fun TopBarLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.button,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    )
}
