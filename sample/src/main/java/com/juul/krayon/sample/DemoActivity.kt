package com.juul.krayon.sample

import android.app.Activity
import android.os.Bundle
import com.juul.krayon.chart.ChartView
import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.data.toCluster
import com.juul.krayon.chart.data.toRectangularDataSet
import com.juul.krayon.chart.render.BarChartRenderer
import com.juul.krayon.kanvas.nextColor
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

    private fun getRandomData(): ClusteredDataSet<Float> {
        val numClusters = Random.nextInt(1, 5)
        val numSeries = Random.nextInt(4, 13)
        return (0 until numClusters).map {
            (0 until numSeries).map {
                Random.nextFloat()
            }.toCluster()
        }.toRectangularDataSet()
    }
}
