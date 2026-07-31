package com.juul.krayon.documentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle

@Immutable
data class Typography(
    val code: TextStyle,
) {

    companion object {
        val Default = Typography(
            code = TextStyle.Default,
        )
    }
}
