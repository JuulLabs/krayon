package com.juul.krayon.shape

/** Repositions the baselines/toplines of the stacked [series] in the given stacking [order]. */
public fun interface StackOffset {
    public operator fun invoke(series: List<Series<*, *>>, order: IntArray)
}

/** Applies a zero baseline; each series is stacked on top of the one below it. */
public object StackOffsetNone : StackOffset {
    override fun invoke(series: List<Series<*, *>>, order: IntArray) {
        val n = series.size
        if (n <= 1) return
        val m = series[order[0]].size
        var s1 = series[order[0]]
        for (i in 1 until n) {
            val s0 = s1
            s1 = series[order[i]]
            for (j in 0 until m) {
                val below = if (s0[j].high.isNaN()) s0[j].low else s0[j].high
                s1[j].low = below
                s1[j].high += below
            }
        }
    }
}

/** Normalizes each column to the range `[0, 1]`, useful for percentage-stacked charts. */
public object StackOffsetExpand : StackOffset {
    override fun invoke(series: List<Series<*, *>>, order: IntArray) {
        val n = series.size
        if (n <= 0) return
        val m = series[0].size
        for (j in 0 until m) {
            var y = 0f
            for (i in 0 until n) {
                val value = series[i][j].high
                if (!value.isNaN()) y += value
            }
            if (y != 0f) for (i in 0 until n) series[i][j].high /= y
        }
        StackOffsetNone(series, order)
    }
}

/** Positive values are stacked above zero and negative values below, ignoring existing offsets. */
public object StackOffsetDiverging : StackOffset {
    override fun invoke(series: List<Series<*, *>>, order: IntArray) {
        val n = series.size
        if (n <= 0) return
        val m = series[order[0]].size
        for (j in 0 until m) {
            var yp = 0f
            var yn = 0f
            for (i in 0 until n) {
                val d = series[order[i]][j]
                val dy = d.high - d.low
                when {
                    dy > 0f -> {
                        d.low = yp
                        yp += dy
                        d.high = yp
                    }
                    dy < 0f -> {
                        d.high = yn
                        yn += dy
                        d.low = yn
                    }
                    else -> {
                        d.low = 0f
                        d.high = dy
                    }
                }
            }
        }
    }
}

/** Centers the stack around zero (streamgraph baseline). */
public object StackOffsetSilhouette : StackOffset {
    override fun invoke(series: List<Series<*, *>>, order: IntArray) {
        val n = series.size
        if (n <= 0) return
        val s0 = series[order[0]]
        val m = s0.size
        for (j in 0 until m) {
            var y = 0f
            for (i in 0 until n) {
                val value = series[i][j].high
                if (!value.isNaN()) y += value
            }
            s0[j].low = -y / 2
            s0[j].high += s0[j].low
        }
        StackOffsetNone(series, order)
    }
}

/** Shifts the baseline to minimize the weighted wiggle of the layers (streamgraph). */
public object StackOffsetWiggle : StackOffset {
    override fun invoke(series: List<Series<*, *>>, order: IntArray) {
        val n = series.size
        if (n <= 0) return
        val s0 = series[order[0]]
        val m = s0.size
        if (m <= 0) return
        var y = 0f
        var j = 1
        while (j < m) {
            var s1 = 0f
            var s2 = 0f
            for (i in 0 until n) {
                val si = series[order[i]]
                val sij0 = si[j].high.orZero()
                val sij1 = si[j - 1].high.orZero()
                var s3 = (sij0 - sij1) / 2
                for (k in 0 until i) {
                    val sk = series[order[k]]
                    s3 += sk[j].high.orZero() - sk[j - 1].high.orZero()
                }
                s1 += sij0
                s2 += s3 * sij0
            }
            s0[j - 1].low = y
            s0[j - 1].high += y
            if (s1 != 0f) y -= s2 / s1
            j++
        }
        s0[m - 1].low = y
        s0[m - 1].high += y
        StackOffsetNone(series, order)
    }
}

private fun Float.orZero(): Float = if (isNaN()) 0f else this
