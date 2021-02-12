package com.juul.krayon.chart.render

import com.juul.krayon.canvas.Canvas
import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.data.DataSet

/**
 * Interface rendering charts. Generally, [DATA] will be simply [DataSet]`<Foo>` or
 * [ClusteredDataSet]`<Bar>` for a chart renderer. Most chart renderers will be composed
 * of several internal, focused renderers with [DATA] as a more specialized type.
 */
public interface Renderer<DATA> {
    /** Render [data] to the [canvas]. */
    public fun <PAINT, PATH> render(data: DATA, canvas: Canvas<PAINT, PATH>)
}
