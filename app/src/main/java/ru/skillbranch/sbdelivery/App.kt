package ru.skillbranch.sbdelivery

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val imageLoader = ImageLoader.Builder(this)
            .crossfade(true)
            .componentRegistry {
                add(SvgDecoder(this@App))
            }
            .build()
        Coil.setImageLoader(imageLoader)
    }
}