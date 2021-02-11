package com.juul.krayon.chart.render

/** Generator for chart labels. */
public fun interface LabelFactory<T> {
    /** Generate a label. If this returns `null`, the label is omitted. */
    public fun createLabel(value: T): CharSequence?
}

/** Type alias of LabelFactory<Int> for documentation purposes: the value represents an index. */
public typealias IndexLabelFactory = LabelFactory<Int>
