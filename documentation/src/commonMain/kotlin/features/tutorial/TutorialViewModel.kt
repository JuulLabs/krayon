package com.juul.krayon.documentation.features.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TutorialViewModel : ViewModel() {

    private var i = 0
    val count = MutableStateFlow(i)

    init {
        viewModelScope.launch {
            while (currentCoroutineContext().isActive) {
                count.value = ++i
                println(count.value)
                delay(1.seconds)
            }
        }
    }
}
