package com.gometro.kmpapp

import com.gometro.buildconfig.Environment
import com.gometro.buildconfig.AppBuildConfig
import com.gometro.buildconfig.Platform
import com.gometro.core.di.PlatformDependencyFactory
import com.gometro.core.di.PlatformDependencyRequest
import com.gometro.network.KConnectivityManager
import org.koin.core.component.KoinComponent

class IosDependencyFactory (
//    private val context: Context
): PlatformDependencyFactory, KoinComponent {

    private val appBuildConfig by lazy {
        object : AppBuildConfig {
            override val environment: Environment = BuildConfig.ENVIRONMENT
            override val platform: Platform = Platform.IOS
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
//
//    private val applicationForegroundManager by lazy {
//        ApplicationForegroundManagerAndroid(
//            context = context
//        )
//    }

    override fun <T> create(request: PlatformDependencyRequest<T>): T {
        return when(request) {
            PlatformDependencyRequest.BuildInfo -> appBuildConfig as T
            PlatformDependencyRequest.ConnectivityManagerRequest -> connectivityManager as T
            PlatformDependencyRequest.ForegroundManager -> applicationForegroundManager as T
        }
    }

}
