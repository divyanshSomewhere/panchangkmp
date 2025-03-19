package com.gometro.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.gometro.base.featurecontracts.Device
import com.gometro.DeviceImplIos
import com.gometro.base.featurecontracts.KotlinToastManager
import com.gometro.base.providers.toast.KotlinToastManagerImpl
import com.gometro.base.utils.DataStorePathHelper
import com.gometro.core.init.ApplicationInitManager
import com.gometro.core.init.ApplicationInitManagerIos
import com.gometro.login.manager.UserProfileAndAuthStoreManagerImpl.Companion.USER_DETAILS_DATA_STORE_NAME
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module


internal actual fun platformModule() = module {
    singleOf(::DeviceImplIos) { bind<Device>() }

    single<DataStore<Preferences>>(qualifier = named(USER_DETAILS_DATA_STORE_NAME)) {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { DataStorePathHelper.producePath(USER_DETAILS_DATA_STORE_NAME) },
            migrations = listOf()
        )
    }

    singleOf(::KotlinToastManagerImpl) { bind<KotlinToastManager>() }
    singleOf(::ApplicationInitManagerIos){ bind<ApplicationInitManager>() }
}