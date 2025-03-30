package com.juul.krayon.documentation

import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.generated.resources.chart_sample_title
import com.juul.krayon.documentation.generated.resources.samples_title
import com.juul.krayon.documentation.generated.resources.tutorial_title
import org.jetbrains.compose.resources.StringResource

enum class Screen(val title: StringResource) {
    Tutorial(Res.string.tutorial_title),
    Samples(Res.string.samples_title),
    SineWaveSample(Res.string.chart_sample_title),
    TreeChartSample(Res.string.chart_sample_title),
}
