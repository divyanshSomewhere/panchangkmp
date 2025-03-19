package com.gometro.kmpapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.gometro.core.di.KoinHelper
import com.gometro.core.init.ApplicationInitManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext

class GoMetroApp: Application() {

    private val androidDependencyFactory by lazy { AndroidDependencyFactory(this) }

    private val applicationInitManager: ApplicationInitManager by inject()

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        System.loadLibrary("sqlcipher")

        KoinHelper.initKoin(
            platformDependencyFactory = androidDependencyFactory
        ) {
            androidContext(this@GoMetroApp)
        }
        applicationInitManager.init()
    }

}
