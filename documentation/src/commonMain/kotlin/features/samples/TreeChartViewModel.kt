package com.juul.krayon.documentation.features.samples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juul.krayon.documentation.generated.resources.Res
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TreeChartViewModel : ViewModel() {

    val code = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            code.value =
                Res.readBytes("files/samples/InteractiveTreeChart.kt").decodeToString()
        }
    }
}
