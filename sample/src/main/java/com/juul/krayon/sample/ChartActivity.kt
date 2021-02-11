package com.juul.krayon.sample

import android.app.Activity
import android.os.Bundle
import com.juul.krayon.chart.ChartView
import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.data.clusterOf
import com.juul.krayon.chart.data.toClusteredDataSet
import com.juul.krayon.chart.render.BarChartRenderer
import com.juul.krayon.chart.style.BarChartStyle
import com.juul.krayon.sample.databinding.ActivityChartBinding

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
    }
}
