package com.juul.krayon.documentation.features.samples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juul.krayon.documentation.generated.resources.Res
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SineWaveViewModel : ViewModel() {

    data class Code(
        val data: String,
        val updater: String,
        val glue: String,
    )

    val code = MutableStateFlow<Code?>(null)

    init {
        viewModelScope.launch {
            code.value = Code(
                data = Res.readBytes("files/samples/data/SineWave.kt").decodeToString(),
                updater = Res.readBytes("files/samples/updaters/LineChart.kt").decodeToString(),
                glue = Res.readBytes("files/samples/MovingSineWaveView.kt").decodeToString(),
            )
        }
    }
}
