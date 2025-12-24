package com.example.albumio

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class AlbumioApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES
        )

    }
}