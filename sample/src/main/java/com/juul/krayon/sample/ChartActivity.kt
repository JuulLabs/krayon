package com.juul.krayon.sample

import android.app.Activity
import android.os.Bundle
import com.juul.krayon.chart.ChartView
import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.data.clusterOf
import com.juul.krayon.chart.data.toCluster
import com.juul.krayon.chart.data.toClusteredDataSet
import com.juul.krayon.chart.data.toRectangularDataSet
import com.juul.krayon.chart.render.BarChartRenderer
import com.juul.krayon.chart.style.BarChartStyle
import com.juul.krayon.sample.databinding.ActivityChartBinding
import kotlin.random.Random

class ChartActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = ChartView.Adapter<ClusteredDataSet<Float>>().apply {
            renderer = BarChartRenderer(BarChartStyle())
            dataSet = clusterOf(1f, 2f, 3f, 4f, 5f, 6f).toClusteredDataSet()
        }
        binding.chartView.adapter = adapter
        binding.chartView.setOnClickListener {
            val numClusters = Random.nextInt(1, 5)
            val numSeries = Random.nextInt(4, 13)
            adapter.dataSet = (0 until numClusters).map {
                (0 until numSeries).map {
                    Random.nextFloat()
                }.toCluster()
            }.toRectangularDataSet()
        }
    }
}
