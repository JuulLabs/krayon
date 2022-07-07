package com.juul.krayon.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.juul.krayon.compose.ElementView

class ComposeLineActivity : AppCompatActivity() {

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
