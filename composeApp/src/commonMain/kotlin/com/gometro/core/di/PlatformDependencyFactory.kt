package com.gometro.core.di

import co.touchlab.skie.configuration.annotations.SealedInterop
import com.gometro.buildconfig.GometroBuildConfig
import com.gometro.foreground.ApplicationForegroundManager
import com.gometro.network.KConnectivityManager

interface PlatformDependencyFactory {

    fun <T>create(request: PlatformDependencyRequest<T>) : T

}

@SealedInterop.Enabled
sealed class PlatformDependencyRequest<out CREATES> {
    data object BuildInfo : PlatformDependencyRequest<GometroBuildConfig>()
    data object ConnectivityManagerRequest : PlatformDependencyRequest<KConnectivityManager>()
    data object ForegroundManager : PlatformDependencyRequest<ApplicationForegroundManager>()
}

