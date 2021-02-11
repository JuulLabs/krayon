package com.juul.krayon.chart.style

import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.Font
import com.juul.krayon.chart.render.IndexLabelFactory

public class BarChartStyle(
    public val seriesColors: Sequence<Color> = defaultSeriesColors(),
    public val axisLabelFont: Font = defaultFont(),
    public val orientation: Orientation = Orientation.Vertical,
    public val clusterKind: ClusterKind = ClusterKind.Grouped,
    public val categoryLabelFactory: IndexLabelFactory = defaultIndexLabelFactory(),
    public val seriesLabelFactory: IndexLabelFactory = defaultIndexLabelFactory(),
    // There will be many more fields. But I don't feel a need to list them all speculatively.
) {

    /** How to represent multiple series within the same cluster. */
    public enum class ClusterKind {
        /** Each series should be its own bar, grouped next to each other. */
        Grouped,

        /** Each series should stacked to make a larger bar. */
        Stacked
    }
}
