package com.juul.krayon.compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

public interface NativeElement {

    @Composable public fun BoxScope.Content(modifier: Modifier = Modifier)
}
