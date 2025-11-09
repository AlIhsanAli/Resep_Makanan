package com.example.resep_makanan

import android.app.Application
import app.rive.runtime.kotlin.core.Rive

class BearLoginApp: Application() {
    override fun onCreate() {
        Rive.init(this)
        super.onCreate()
    }
}