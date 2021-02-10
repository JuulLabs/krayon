package com.juul.krayon.chart.render

/** Generator for chart labels. */
public fun interface LabelFactory<T> {
    /** Generate a label. If this returns `null`, the label is omitted. */
    public fun createLabel(value: T): CharSequence?
}

/** It's basically `(Int) -> String?` but now you know what the `Int` represents. */
public fun interface IndexLabelFactory : LabelFactory<Int>
