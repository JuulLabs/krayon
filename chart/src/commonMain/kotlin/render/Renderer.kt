package com.juul.krayon.chart.render

import com.juul.krayon.chart.data.ClusteredDataSet
import com.juul.krayon.chart.data.DataSet
import com.juul.krayon.kanvas.Kanvas

/**
 * Interface rendering charts. Generally, [DATA] will be simply [DataSet]`<Foo>` or
 * [ClusteredDataSet]`<Bar>` for a chart renderer. Most chart renderers will be composed
 * of several internal, focused renderers with [DATA] as a more specialized type.
 */
public interface Renderer<DATA> {

    /**
     * Render [data] to the [canvas]. Generally, this render is optimized for quality and not speed.
     * If this is used as a live UI component, it is important to call this in a background thread,
     * cache results, or take other precautions to avoid UI hangs.
     */
    public fun <PATH> render(data: DATA, canvas: Kanvas<PATH>)
}
