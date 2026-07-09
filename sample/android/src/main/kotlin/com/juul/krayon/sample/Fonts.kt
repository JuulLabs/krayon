package com.juul.krayon.sample

import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.addFontAssociation
import com.juul.krayon.kanvas.serif

object Fonts {
    val robotoSlab by lazy {
        addFontAssociation("roboto_slab", R.font.roboto_slab)
        Font("roboto_slab", serif)
    }
}
