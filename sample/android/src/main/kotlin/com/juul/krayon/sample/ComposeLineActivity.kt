package com.juul.krayon.sample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.juul.krayon.compose.ElementView
import com.juul.krayon.sample.data.movingSineWave
import com.juul.krayon.sample.updaters.lineChart

class ComposeLineActivity : AppCompatActivity() {

    @SuppressLint("RememberReturnType") // Lint incorrect fails saying `remember` is returning `Unit`.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElementView(
                remember { movingSineWave() },
                ::lineChart,
                Modifier.fillMaxSize(),
            )
        }
    }
}
