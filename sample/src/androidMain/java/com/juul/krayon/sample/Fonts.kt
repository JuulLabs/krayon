package com.juul.krayon.sample

import com.juul.krayon.canvas.Font
import com.juul.krayon.canvas.addFontAssociation
import com.juul.krayon.canvas.serif

object Fonts {
    val robotoSlab by lazy {
        addFontAssociation("Roboto Slab", R.font.roboto_slab)
        Font("Roboto Slab", serif)
    }
}
