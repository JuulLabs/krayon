package com.juul.krayon.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.juul.krayon.color.blue
import com.juul.krayon.color.red
import com.juul.krayon.compose.ElementView
import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.element.UpdateElement
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.sample.databinding.ActivityInteractiveBinding
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import kotlinx.coroutines.flow.MutableStateFlow

class InteractiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityInteractiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val traditionalData = MutableStateFlow(listOf(false, false, false))
        val composeData = mutableStateOf(listOf(false, false, false))

        binding.interactiveView.adapter = ElementViewAdapter(traditionalData, getUpdater { traditionalData.value = it })
        binding.composeView.setContent {
            ElementView(
                composeData,
                (getUpdater { composeData.value = it })::update,
                Modifier.fillMaxSize()
            )
        }
    }
}

private fun getUpdater(sideEffect: (List<Boolean>) -> Unit) = UpdateElement<List<Boolean>> { root, width, height, data ->
    root.asSelection()
        .selectAll(CircleElement)
        .data(data)
        .join {
            append(CircleElement).each { (_, i) ->
                radius = 100f
            }
        }
        .each { (d, i) ->
            centerX = (width / 5) * (1 + i)
            centerY = height / 2
            paint = Paint.Fill((if (d) blue else red).copy(alpha = 128))
            onClick = { // captures `data` so we need to reset it every update
                val newValue = data
                    .toBooleanArray()
                    .apply { this[i] = !this[i] }
                    .toList()
                sideEffect(newValue)
            }
        }

}
