package com.juul.krayon.core

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

@InternalKrayonApi
public class KrayonInitializer : Initializer<Krayon> {

    override fun create(context: Context): Krayon {
        application = context.applicationContext as Application
        return Krayon
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
