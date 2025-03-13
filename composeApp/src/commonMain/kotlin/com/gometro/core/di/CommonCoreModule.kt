package com.gometro.core.di

import com.gometro.base.providers.CoroutineContextProvider
import com.gometro.base.providers.CoroutineContextProviderImpl
import com.gometro.base.domain.UpdateGometroBuildConfigUseCase
import com.gometro.base.providers.toast.KotlinToastManager
import com.gometro.base.providers.toast.KotlinToastManagerImpl
import com.gometro.buildconfig.GometroBuildConfig
import com.gometro.constants.DataStoreKeys.ENV_CHANGE_STORE_KEY
import com.gometro.constants.DataStoreKeys.GENERAL_PURPOSE_COMMON_DATASTORE_KEY
import com.gometro.core.datastore.LocalDataStore
import com.gometro.foreground.ApplicationForegroundManager
import com.gometro.homescreen.presentation.HomeScreenViewModel
import com.gometro.kmpapp.data.InMemoryMuseumStorage
import com.gometro.kmpapp.data.KtorMuseumApi
import com.gometro.kmpapp.data.MuseumApi
import com.gometro.kmpapp.data.MuseumRepository
import com.gometro.kmpapp.data.MuseumStorage
import com.gometro.kmpapp.screens.detail.DetailViewModel
import com.gometro.kmpapp.screens.list.ListViewModel
import com.gometro.logger.GometroLog
import com.gometro.logger.GometroLogger
import com.gometro.network.KConnectivityManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun getSharedCoreModule(platformDependencyFactory: PlatformDependencyFactory) = module {
    includes(commonModule(platformDependencyFactory), platformModule(), viewModelModule())
}

private fun commonModule(platformDependencyFactory: PlatformDependencyFactory) = module {
    single<PlatformDependencyFactory> { platformDependencyFactory }
    factory {
        UpdateGometroBuildConfigUseCase(
            envChangeStore = get(named(ENV_CHANGE_STORE_KEY)),
            chaloBuildConfig = get()
        )
    }
    single<ApplicationForegroundManager> {
        get<PlatformDependencyFactory>().create(PlatformDependencyRequest.ForegroundManager)
    }

    factoryOf(::CoroutineContextProviderImpl) { bind<CoroutineContextProvider>() }
    singleOf(::KotlinToastManagerImpl) { bind<KotlinToastManager>() }


//    factory {
//        val token = when(get<GometroBuildConfig>().environment){
//            Environment.PRODUCTION -> AppConstants.MIXPANEL_TOKEN_PROD
//            Environment.DEVELOPMENT -> AppConstants.MIXPANEL_TOKEN_DEBUG
//            Environment.STAGING -> AppConstants.MIXPANEL_TOKEN_STAGING
//        }
//        MixpanelSenderConfig(
//            deviceId = get<Device>().getDeviceId(),
//            token = token
//        )
//    }
    single<LocalDataStore> {
        LocalDataStore(get(named(GENERAL_PURPOSE_COMMON_DATASTORE_KEY)))
    }
//    factory<ChaloSocket> {
//        get<PlatformDependencyFactory>().create(PlatformDependencyRequest.ChaloSocketRequest)
//    }
    factory<KConnectivityManager> {
        get<PlatformDependencyFactory>().create(PlatformDependencyRequest.ConnectivityManagerRequest)
    }
    single<GometroBuildConfig> {
        get<PlatformDependencyFactory>().create(PlatformDependencyRequest.BuildInfo)
    }


    single<MuseumApi> { KtorMuseumApi(get()) }
    single<MuseumStorage> { InMemoryMuseumStorage() }
    single {
        MuseumRepository(get(), get()).apply {
            initialize()
        }
    }

    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                // TODO Fix API so it serves application/json
                json(json, contentType = ContentType.Any)
            }
        }
    }

//    single<RouteDao> { get<AppDatabase>().routeDao }
//    single<RouteRecentCityItineraryDao> { get<AppDatabase>().routeRecentCityItineraryDao }
//    single<RecentPlacesCityItineraryDao> { get<AppDatabase>().recentPlacesCityItineraryDao }
//    single<RecentStopBasedTripCityItineraryDao> { get<AppDatabase>().recentStopBasedTripCityItineraryDao }
//    single<RecentStopCityItineraryEntityDao> { get<AppDatabase>().recentStopCityItineraryEntityDao }
//    single<RecentTripCityItineraryDao> { get<AppDatabase>().recentTripCityItineraryDao }
//    single<PremiumBusTripDao> { get<AppDatabase>().premiumBusTripDao }
//    single<PremiumBusDao> { get<AppDatabase>().premiumBusDao }
//    single<SuperPassDao> { get<AppDatabase>().superPassDao }
//    single<InstantTicketDao> { get<AppDatabase>().instantTicketDao }
//    single<TITOValidationDao> { get<AppDatabase>().titoValidationDao }
//    single<OndcTicketDao> { get<AppDatabase>().ondcTicketDao }
//
//    factory<ChaloMapMarkerInfoWindowProvider> { get<PlatformDependencyFactory>().create(PlatformDependencyRequest.MapInfoWindowProvider) }
//    factory<LatLngBoundsUtils> { get<PlatformDependencyFactory>().create(PlatformDependencyRequest.LLBUtils) }
//    factory<RouteMapLiveVehicleIconHelper> { get<PlatformDependencyFactory>().create(PlatformDependencyRequest.RouteMapLiveVehicleHelper) }
//    single<NearbyBusMarkerIconHelper> {
//        get<PlatformDependencyFactory>().create(PlatformDependencyRequest.NearbyBusMarkerIconHelperRequest)
//    }
//    factory<ChaloMapLiveBusMarkerHelper> { get<PlatformDependencyFactory>().create(PlatformDependencyRequest.ChaloLiveBusMarkerHelper) }
//
//    factory<HtmlTextUIHelper> { get<PlatformDependencyFactory>().create(PlatformDependencyRequest.HtmlTextHelper) }
}

private fun viewModelModule() = module {
    factoryOf(::HomeScreenViewModel)
    factoryOf(::ListViewModel)
    factoryOf(::DetailViewModel)
}


internal expect fun platformModule(): Module
