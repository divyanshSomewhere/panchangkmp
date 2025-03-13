package com.gometro.core.init

import com.gometro.base.providers.CoroutineContextProvider
import com.gometro.buildconfig.GometroBuildConfig
import com.gometro.core.di.PlatformDependencyFactory
import com.gometro.database.GometroAppDatabase
import com.gometro.network.utils.GometroLoggerAndroid

class ApplicationInitManagerAndroid(
    coroutineContextProvider: CoroutineContextProvider,
    platformDependencyFactory: PlatformDependencyFactory,
    gometroAppDatabase: GometroAppDatabase,
    gometroBuildConfig: GometroBuildConfig
): ApplicationInitManager(

    coroutineContextProvider = coroutineContextProvider,
//    private val cityProvider: CityProvider,
//    private val chaloConfigFeature: ChaloConfigFeature,
//    private val analyticsContract: AnalyticsContract,
//    private val validateChaloTimeCacheUseCase: ValidateChaloTimeCacheUseCase,
//    private val chaloTimeRefreshManagerUseCase: ChaloTimeRefreshManagerUseCase,
//    private val fetchAndUpdateMetaDataPropsUseCase: FetchAndUpdateMetaDataPropsUseCase,
    gometroBuildConfig = gometroBuildConfig,
    platformDependencyFactory = platformDependencyFactory,
    chaloCoreDatabase = gometroAppDatabase,
) {
    override fun platformPreInit() {
        registerGometroLogger(logger = GometroLoggerAndroid())
    }

    override fun platformPostInit() {}
}