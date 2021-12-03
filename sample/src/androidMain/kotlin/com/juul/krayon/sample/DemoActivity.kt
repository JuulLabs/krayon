package com.juul.krayon.sample

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.juul.krayon.kanvas.AndroidKanvas
import com.juul.krayon.sample.databinding.ActivityDemoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var job: Job? = null
        binding.sineView.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
            job?.cancel()
            job = lifecycleScope.launch {
                val bitmap: Bitmap
                withContext(Dispatchers.IO) {
                    bitmap = Bitmap.createBitmap(
                        binding.sineView.width,
                        binding.sineView.height,
                        Bitmap.Config.ARGB_8888
                    )
                    AndroidKanvas(
                        context = this@DemoActivity,
                        canvas = Canvas(bitmap),
                        scalingFactor = applyDimension(COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
                    ).renderSineWave()
                }
                binding.sineView.setImageBitmap(bitmap)
            }
        }
    }
}
