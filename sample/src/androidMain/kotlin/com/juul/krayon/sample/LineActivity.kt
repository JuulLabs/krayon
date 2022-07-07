package com.juul.krayon.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.sample.databinding.ActivityLineBinding

class LineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sineView.adapter = ElementViewAdapter(
            dataSource = movingSineWave(),
            updater = ::lineChart,
        )
    }
}
