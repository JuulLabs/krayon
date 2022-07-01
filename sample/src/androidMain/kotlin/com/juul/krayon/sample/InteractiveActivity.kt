package com.juul.krayon.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.juul.krayon.compose.ElementView
import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.sample.databinding.ActivityInteractiveBinding

class InteractiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityInteractiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.interactiveView.adapter = interactiveTreeChart().let { (flow, update) ->
            ElementViewAdapter(flow, update)
        }

        binding.composeView.setContent {
            val (flow, update) = remember { interactiveTreeChart() }
            ElementView(flow, update, Modifier.fillMaxSize())
        }
    }
}
