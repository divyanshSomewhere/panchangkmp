package com.gometro.core.init

import com.gometro.base.featurecontracts.CoroutineContextProvider
import com.gometro.buildconfig.AppBuildConfig
import com.gometro.core.di.PlatformDependencyFactory
import com.gometro.core.iosutils.AppLoggerIos
import com.gometro.database.AppDatabase

class ApplicationInitManagerIos(
    coroutineContextProvider: CoroutineContextProvider,
    platformDependencyFactory: PlatformDependencyFactory,
    appDatabase: AppDatabase,
    appBuildConfig: AppBuildConfig
): ApplicationInitManager(

    coroutineContextProvider = coroutineContextProvider,
//    private val cityProvider: CityProvider,
//    private val chaloConfigFeature: ChaloConfigFeature,
//    private val analyticsContract: AnalyticsContract,
//    private val validateChaloTimeCacheUseCase: ValidateChaloTimeCacheUseCase,
//    private val chaloTimeRefreshManagerUseCase: ChaloTimeRefreshManagerUseCase,
//    private val fetchAndUpdateMetaDataPropsUseCase: FetchAndUpdateMetaDataPropsUseCase,
    appBuildConfig = appBuildConfig,
    platformDependencyFactory = platformDependencyFactory,
    chaloCoreDatabase = appDatabase,
) {
    override fun platformPreInit() {
        registerGometroLogger(logger = AppLoggerIos())
    }

    override fun platformPostInit() {
    }
}