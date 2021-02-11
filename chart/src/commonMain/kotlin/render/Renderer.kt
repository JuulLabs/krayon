package com.juul.krayon.chart.render

import com.juul.krayon.canvas.Canvas
import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.data.DataSet

/**
 * Interface rendering charts. Generally, [DATA] will be simply [DataSet]`<Foo>` or
 * [ClusteredDataSet]`<Bar>`, although it is intentionally open for other subclasses.
 */
public interface Renderer<DATA> where DATA : DataSet<*> {
    /** Render [dataSet] to the [canvas]. */
    public fun <PAINT, PATH> render(dataSet: DATA, canvas: Canvas<PAINT, PATH>)
}
