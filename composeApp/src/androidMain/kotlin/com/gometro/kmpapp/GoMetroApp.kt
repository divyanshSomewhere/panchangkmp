package com.gometro.kmpapp

import android.app.Application
import com.gometro.kmpapp.di.initKoin

class GoMetroApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
