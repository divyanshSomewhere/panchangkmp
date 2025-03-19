package com.gometro.core.di

import com.gometro.base.analytics.AnalyticsContractImpl
import com.gometro.base.analytics.AnalyticsManager
import com.gometro.base.analytics.AnalyticsManager.Companion.ANALYTICS_DATA_STORE_NAME
import com.gometro.base.analytics.senders.AnalyticsSendersProvider
import com.gometro.base.basefeatureimpl.BasicInfoContractImpl
import com.gometro.base.featurecontracts.CoroutineContextProvider
import com.gometro.base.featurecontracts.CoroutineContextProviderImpl
import com.gometro.base.domain.UpdateGometroBuildConfigUseCase
import com.gometro.base.featurecontracts.AnalyticsContract
import com.gometro.base.featurecontracts.BasicInfoContract
import com.gometro.base.featurecontracts.KotlinToastManager
import com.gometro.base.providers.AppUrlProviderImpl
import com.gometro.base.providers.toast.KotlinToastManagerImpl
import com.gometro.base.userprofile.UserProfileAndAuthStoreManager
import com.gometro.buildconfig.AppBuildConfig
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
import com.gometro.login.data.local.LoginLocalDataSource
import com.gometro.login.data.local.LoginLocalDataSourceImpl
import com.gometro.login.data.remote.LoginRemoteDataSource
import com.gometro.login.data.remote.LoginRemoteDataSourceImpl
import com.gometro.login.data.repository.LoginRepository
import com.gometro.login.data.repository.LoginRepositoryImpl
import com.gometro.login.domain.ExtractOtpFromSmsContentUseCase
import com.gometro.login.domain.JwtEncodedAccessTokenParser
import com.gometro.login.domain.LogoutUserUseCase
import com.gometro.login.domain.ParseAndStoreTokensUseCase
import com.gometro.login.domain.RefreshAuthTokensForUserUseCase
import com.gometro.login.domain.SendOtpForLoginUseCase
import com.gometro.login.domain.SyncAndUpdateAnalyticsPropertiesAfterLoginUseCase
import com.gometro.login.domain.VerifyLoginSuccessOnServerAndHandleTokensUseCase
import com.gometro.login.manager.AuthSecurityManagerImpl
import com.gometro.login.manager.UserProfileAndAuthStoreManagerImpl
import com.gometro.login.manager.UserProfileAndAuthStoreManagerImpl.Companion.USER_DETAILS_DATA_STORE_NAME
import com.gometro.login.manager.UserProfileDetailsProviderImpl
import com.gometro.login.phonenumberhint.PhoneNumberHintHandlerProvider
import com.gometro.login.phonenumberhint.PhoneNumberHintHandlerProviderImpl
import com.gometro.login.phonenumberhint.PhoneNumberHintHandlerSetter
import com.gometro.network.AppUrlProvider
import com.gometro.network.AuthSecurityManager
import com.gometro.network.HttpClientHelper
import com.gometro.network.KConnectivityManager
import com.gometro.network.NetworkManager
import com.gometro.network.NetworkManagerImpl
import com.gometro.network.config.NetworkConfig
import com.gometro.network.mapper.GenericNetworkExceptionMapper
import com.gometro.network.rest.AppRestClient
import com.gometro.network.rest.AppRestClientManager
import com.gometro.userprofile.data.local.UserProfileLocalDataSource
import com.gometro.userprofile.data.local.UserProfileLocalDataSourceImpl
import com.gometro.userprofile.data.remote.UserProfileRemoteDataSource
import com.gometro.userprofile.data.remote.UserProfileRemoteDataSourceImpl
import com.gometro.userprofile.data.repository.UserProfileRepository
import com.gometro.userprofile.data.repository.UserProfileRepositoryImpl
import com.gometro.userprofile.domain.UpdateUserProfileUseCase
import com.gometro.userprofile.domain.UserProfileDetailsProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun getSharedCoreModule(platformDependencyFactory: PlatformDependencyFactory) = module {
    includes(commonModule(platformDependencyFactory), platformModule(), viewModelModule())
}

private fun commonModule(platformDependencyFactory: PlatformDependencyFactory) = module {
    singleOf(::KotlinToastManagerImpl) { bind<KotlinToastManager>() }
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
    singleOf(::BasicInfoContractImpl) { bind<BasicInfoContract>()}

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
    single<AppBuildConfig> {
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


    //////////////////////////////////////////
    // Analytics related dependencies
    //////////////////////////////////////////

    single<AnalyticsManager> {
        AnalyticsManager(
            senders = get<AnalyticsSendersProvider>().provideAnalyticsSenders(),
            dataStore = get(named(ANALYTICS_DATA_STORE_NAME)),
            basicInfoContract = get()
        )
    }
    singleOf(::AnalyticsContractImpl) { bind<AnalyticsContract>()}


    //////////////////////////////////////////
    // Login related dependencies
    //////////////////////////////////////////

    factory<UserProfileAndAuthStoreManager> {
        UserProfileAndAuthStoreManagerImpl(get(named(USER_DETAILS_DATA_STORE_NAME)))
    }
    singleOf(::PhoneNumberHintHandlerProviderImpl) { binds(listOf(PhoneNumberHintHandlerProvider::class, PhoneNumberHintHandlerSetter::class)) }
    singleOf(::LoginLocalDataSourceImpl) { bind<LoginLocalDataSource>() }
    singleOf(::LoginRemoteDataSourceImpl) { bind<LoginRemoteDataSource>() }
    single<UserProfileAndAuthStoreManager> {
        UserProfileAndAuthStoreManagerImpl(get(named(USER_DETAILS_DATA_STORE_NAME)))
    }
    singleOf(::AuthSecurityManagerImpl) { bind<AuthSecurityManager>() }
    singleOf(::LoginRepositoryImpl) { bind<LoginRepository>() }
    singleOf(::RefreshAuthTokensForUserUseCase)
    singleOf(::JwtEncodedAccessTokenParser)
    singleOf(::ParseAndStoreTokensUseCase)
    singleOf(::SendOtpForLoginUseCase)
    singleOf(::VerifyLoginSuccessOnServerAndHandleTokensUseCase)
    singleOf(::ExtractOtpFromSmsContentUseCase)
    singleOf(::LogoutUserUseCase)
    singleOf(::SyncAndUpdateAnalyticsPropertiesAfterLoginUseCase)

    //////////////////////////////////////////
    // User profile related dependencies
    //////////////////////////////////////////

    singleOf(::UserProfileRemoteDataSourceImpl) { bind<UserProfileRemoteDataSource>()}
    singleOf(::UserProfileLocalDataSourceImpl) { bind<UserProfileLocalDataSource>()}
    singleOf(::UserProfileRepositoryImpl) { bind<UserProfileRepository>()}
    singleOf(::UserProfileDetailsProviderImpl) { bind<UserProfileDetailsProvider>() }
    singleOf(::UpdateUserProfileUseCase)

    //////////////////////////////////////////
    // Network related dependencies
    //////////////////////////////////////////

    factory {
        NetworkConfig(get(named(NetworkConfig.NETWORK_CONFIG_FILE)))
    }
    singleOf(::AppUrlProviderImpl) { bind<AppUrlProvider>() }

    singleOf(::NetworkManagerImpl) { bind<NetworkManager>() }
    single<AppRestClient>(qualifier = named(AppRestClientManager.CLIENT_PRIORITY_LOW)) {
        AppRestClient(
            httpClient = HttpClientHelper.getHttpClientDefinitionForPriority(
                httpClientEngine = get(named(AppRestClientManager.CLIENT_PRIORITY_LOW)),
                commonHeaderProvider = get(),
                authSecurityManager = get()
            )
        )
    }
    single<AppRestClient>(qualifier = named(AppRestClientManager.CLIENT_PRIORITY_MEDIUM)) {
        AppRestClient(
            httpClient = HttpClientHelper.getHttpClientDefinitionForPriority(
                httpClientEngine = get(named(AppRestClientManager.CLIENT_PRIORITY_MEDIUM)),
                commonHeaderProvider = get(),
                authSecurityManager = get()
            )
        )
    }
    single<AppRestClient>(qualifier = named(AppRestClientManager.CLIENT_PRIORITY_HIGH)) {
        AppRestClient(
            httpClient = HttpClientHelper.getHttpClientDefinitionForPriority(
                httpClientEngine = get(named(AppRestClientManager.CLIENT_PRIORITY_HIGH)),
                commonHeaderProvider = get(),
                authSecurityManager = get()
            )
        )
    }
    factoryOf(::GenericNetworkExceptionMapper)

}

private fun viewModelModule() = module {
    factoryOf(::HomeScreenViewModel)
    factoryOf(::ListViewModel)
    factoryOf(::DetailViewModel)
}


internal expect fun platformModule(): Module
