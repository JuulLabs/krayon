package com.juul.krayon.sample

import android.app.Activity
import android.os.Bundle
import com.juul.krayon.canvas.nextColor
import com.juul.krayon.chart.ChartView
import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.render.BarChartRenderer
import com.juul.krayon.sample.databinding.ActivityDemoBinding
import kotlin.random.Random

class DemoActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.customCanvasView.setOnClickListener {
            binding.customCanvasView.circleColor = Random.nextColor()
        }

        val adapter = ChartView.Adapter<ClusteredDataSet<Float>>().apply {
            renderer = BarChartRenderer()
            dataSet = getRandomData()
        }
        binding.chartView.adapter = adapter
        binding.chartView.setOnClickListener {
            adapter.dataSet = getRandomData()
        }
    }
}
