package com.juul.krayon.documentation.features.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juul.krayon.documentation.generated.resources.Res
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TutorialViewModel : ViewModel() {

    data class Code(
        val line1: String,
    )

    val code = MutableStateFlow<Code?>(null)

    init {
        viewModelScope.launch {
            code.value = Code(
                line1 = Res.readBytes("files/samples/tutorial/Line1.kt").decodeToString(),
            )
        }
    }
}
