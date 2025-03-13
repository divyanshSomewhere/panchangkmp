package com.gometro.kmpapp

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.gometro.buildconfig.Environment
import com.gometro.buildconfig.GometroBuildConfig
import com.gometro.buildconfig.Platform
import com.gometro.buildconfig.ProductFlavor
import com.gometro.core.di.PlatformDependencyFactory
import com.gometro.core.di.PlatformDependencyRequest
import com.gometro.foreground.ApplicationForegroundManagerAndroid
import com.gometro.network.KConnectivityManager
import com.gometro.network.KConnectivityManagerAndroid
import org.koin.core.component.KoinComponent

class AndroidDependencyFactory (
    private val context: Context
): PlatformDependencyFactory, KoinComponent {

    private val gometroBuildConfig by lazy {
        object : GometroBuildConfig {
            override val environment: Environment = BuildConfig.ENVIRONMENT
            override val platform: Platform = Platform.ANDROID
            override val versionCode: Int = BuildConfig.VERSION_CODE
            override val isDebugBuild: Boolean = BuildConfig.DEBUG
            override val deviceModel: String = Build.MODEL
            override val osVersion: Int = Build.VERSION.SDK_INT
        }
    }

    private val connectivityManager: KConnectivityManager
    get() {
        return KConnectivityManagerAndroid(
            connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        )
    }

    private val applicationForegroundManager by lazy {
        ApplicationForegroundManagerAndroid(
            context = context
        )
    }

    override fun <T> create(request: PlatformDependencyRequest<T>): T {
        return when(request) {
            PlatformDependencyRequest.BuildInfo -> gometroBuildConfig as T
            PlatformDependencyRequest.ConnectivityManagerRequest -> connectivityManager as T
            PlatformDependencyRequest.ForegroundManager -> applicationForegroundManager as T
        }
    }

}
