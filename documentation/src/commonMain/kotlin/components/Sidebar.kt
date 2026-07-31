package com.juul.krayon.documentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.Screen

private val sections = listOf(
    "Introduction" to listOf(
        Screen.Home,
        Screen.GettingStarted,
        Screen.D3,
    ),
    "Concepts" to listOf(
        Screen.Selections,
        Screen.Scales,
        Screen.Shapes,
        Screen.Axes,
        Screen.Hierarchy,
        Screen.ColorConcept,
        Screen.Rendering,
        Screen.Interaction,
    ),
    "Examples" to listOf(
        Screen.Gallery,
    ),
)

@Composable
fun Sidebar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.03f))
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp, horizontal = 8.dp),
    ) {
        sections.forEach { (header, screens) ->
            Text(
                text = header.uppercase(),
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
            screens.forEach { screen ->
                val selected = currentRoute == screen.route ||
                    (screen == Screen.Gallery && currentRoute?.startsWith("gallery") == true)
                SidebarEntry(screen.title, selected) { onNavigate(screen) }
            }
        }
    }
}

@Composable
private fun SidebarEntry(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (selected) MaterialTheme.colors.primary.copy(alpha = 0.12f) else Color.Transparent
    val textColor = if (selected) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.onSurface
    Text(
        text = title,
        style = MaterialTheme.typography.body2,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        color = textColor,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}
