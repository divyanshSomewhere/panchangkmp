package com.gometro.core.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

object KoinHelper {

    fun initKoin(
        platformDependencyFactory: PlatformDependencyFactory,
        initialApplicationSetup: KoinApplication.() -> Unit = {}
    ) {
        startKoin {
            initialApplicationSetup.invoke(this)
            modules(
                getSharedCoreModule(platformDependencyFactory),
//                getSharedNetworkModule(),
//                getSharedChaloBaseModule(),
//                getSharedLoginModule(),
//                getSharedOnboardingModule(),
//                getSharedAnalyticsModule(),
//                getSharedVaultModule(),
//                getSharedSecurityModule(),
//                getSharedWalletFrameworkModule(),
//                getSharedWalletModule(),
//                getSharedCheckoutModule(),
//                getSharedLiveTrackingModule(),
//                getSharedHomeModule(),
//                getSharedFrameworkCityDataModule(),
//                getSharedProductBookingModule()
            )
        }
    }
}