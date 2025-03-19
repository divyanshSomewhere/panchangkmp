package com.gometro.core.init

import com.gometro.base.featurecontracts.CoroutineContextProvider
import com.gometro.buildconfig.AppBuildConfig
import com.gometro.core.di.PlatformDependencyFactory
import com.gometro.database.AppDatabase
import com.gometro.logger.AppLog
import com.gometro.logger.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class ApplicationInitManager(
    coroutineContextProvider: CoroutineContextProvider,
//    private val cityProvider: CityProvider,
//    private val chaloConfigFeature: ChaloConfigFeature,
//    private val analyticsContract: AnalyticsContract,
//    private val validateChaloTimeCacheUseCase: ValidateChaloTimeCacheUseCase,
//    private val chaloTimeRefreshManagerUseCase: ChaloTimeRefreshManagerUseCase,
//    private val fetchAndUpdateMetaDataPropsUseCase: FetchAndUpdateMetaDataPropsUseCase,
    private val platformDependencyFactory: PlatformDependencyFactory,
    protected val appBuildConfig: AppBuildConfig,
    private val chaloCoreDatabase: AppDatabase,
//    private val encryptionFeature: EncryptionFeature,
//    private val walletSyncHelper: WalletSyncHelper,
//    private val walletDao: WalletDao,
//    private val tripPlannerItineraryLiveInfoManager: TripPlannerItineraryLiveInfoManager,
//    private val recentRouteRecentCityItineraryDao: RouteRecentCityItineraryDao
) {

    protected val scope by lazy { CoroutineScope(coroutineContextProvider.io) }

    fun init() {
//        CrashlyticsSetupManager.setupCrashlytics()
//        analyticsContract.setupAnalytics()

        platformPreInit()

//        updateMetaPropsIfNotUpdated()
//        checkIfMetaPropsAvailableAndRefreshChaloConfig()
//        validateChaloTimeInCache()
//        syncEncryptionKeys()
//        syncWallet()
        coreTesting()
//        crtsTesting()

        platformPostInit()

//        appDbTesting()
    }

    /**
     * Gets called before setting up other features common init.
     * Note: This is not the first init call, crashlytics, analytics init are called before this
     */
    abstract fun platformPreInit()

    /**
     * Gets called after setting up other features common init
     */
    abstract fun platformPostInit()

    protected fun registerGometroLogger(logger: AppLogger) {
        AppLog.register(
            logger = logger,
            isDebugBuild = appBuildConfig.isDebugBuild
        )
    }

    private fun coreTesting() {
        scope.launch {
//            println("CoreDb: wallet ${walletDaoImpl.getWallet("9971564574")}")
//            println("CoreDb: wallet transaction: ${walletDaoImpl.getAllWalletTransactions("9971564574")}")
//            encryptionFeature.fetchEncryptionKeys("")

            val key = "${(0..20).random()} --- ${(50..100).random()}"
            val value = key.plus("--- ${(20..50).random()}")
//            println("CoreDb: inserting: ${chaloCoreDatabase.configQueries.insert(key, value)}")
        }
    }

//    private fun crtsTesting() {
//        scope.launch {
//            launch {
//                tripPlannerItineraryLiveInfoManager.result.collect { result ->
//                    ChaloLog.debug("SocketStuff", "collect: ${result.map { it.validEta }}")
//                }
//            }
//            launch {
//                ChaloLog.debug("SocketStuff", "adding itinerary")
//                tripPlannerItineraryLiveInfoManager.addItinerariesRequests(
//                    listOf(ItineraryLiveInfoRequest(
//                        id = "Sfgdfgd",
//                        routeId = "aNdCDhXx",
//                        stopId = "LrZOBuFp",
//                        minimumArrivalTimeInMillis = 0
//                    ))
//                )
//            }
//        }
//    }

//    private fun checkIfMetaPropsAvailableAndRefreshChaloConfig() {
//        scope.launch {
//            if (cityProvider.isMetaUpdated.value) {
//                chaloConfigFeature.refreshCache(true)
//            }
//        }
//    }

//    private fun validateChaloTimeInCache() {
//        scope.launch {
//            validateChaloTimeCacheUseCase.invoke()
//        }
//    }

    // TODO::KSHITIJ - move this with actual logic in splash screen later
//    private fun updateMetaPropsIfNotUpdated() {
//        scope.launch {
//            if (!cityProvider.isMetaUpdated.value) {
//                fetchAndUpdateMetaDataPropsUseCase.invoke()
//            }
//        }
//    }

    // TODO::KSHITIJ - move this to homescreen and worker later
//    private fun syncEncryptionKeys() {
//        scope.launch { encryptionFeature.fetchEncryptionKeys("HomeActivity") }
//    }

    // TODO::KSHITIJ - move this to homescreen and worker later
//    private fun syncWallet() {
//        scope.launch {
//            walletSyncHelper.syncWallet(true)
//        }
//    }

//    private fun appDbTesting() {
//        scope.launch {
//            ChaloLog.debug("AppDbStuff", "current: ${recentRouteRecentCityItineraryDao.getAllRouteCityItineraryForCity("mumbai")}")
//            recentRouteRecentCityItineraryDao.insertRouteCityItinerary(
//                RecentRouteEntity(
//                    routeId = "route123",
//                    cityId = "mumbai",
//                    routeName = "Route123",
//                    startStopName = "Start 1",
//                    endStopName = "End 1",
//                    agency = "mumbai",
//                    isAcBus = true,
//                    spfList = null,
//                    isFreeRideAvailable = false,
//                    transitMode = ChaloTransitMode.BUS,
//                    recentsSpecialEntityStatus = RecentsSpecialEntityStatus.NONE,
//                    accessTime = -1,
//                    accessCount = 0
//                )
//            )
//        }
//    }

    companion object : KoinComponent {
        val instance: ApplicationInitManager by inject()
    }

}