package com.juul.krayon.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.juul.krayon.sample.databinding.ActivityDirectoryBinding

class DirectoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDirectoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLine.setNavigateOnClick<LineActivity>()
        binding.buttonPie.setNavigateOnClick<PieActivity>()
    }

    private inline fun <reified T : Activity> View.setNavigateOnClick() {
        setOnClickListener {
            startActivity(Intent(this@DirectoryActivity, T::class.java))
        }
    }
}
