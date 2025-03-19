package com.gometro.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.gometro.base.featurecontracts.Device
import com.gometro.DeviceImplAndroid
import com.gometro.base.featurecontracts.PermissionHandler
import com.gometro.base.featurecontracts.SystemHelper
import com.gometro.base.permissions.handler.PermissionHandlerAndroid
import com.gometro.base.utils.SystemHelperAndroidImpl
import com.gometro.constants.DataStoreKeys.ENV_CHANGE_STORE_KEY
import com.gometro.core.database.AppDatabaseAndroidImpl
import com.gometro.core.init.ApplicationInitManager
import com.gometro.core.init.ApplicationInitManagerAndroid
import com.gometro.database.AppDatabase
import com.gometro.logger.AppLogger
import com.gometro.core.androidutils.AppLoggerAndroid
import com.gometro.login.manager.UserProfileAndAuthStoreManagerImpl.Companion.USER_DETAILS_DATA_STORE_NAME
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module


internal actual fun platformModule(): Module {
    return module {
        single(qualifier = named(ENV_CHANGE_STORE_KEY)) {
            PreferenceDataStoreFactory.createWithPath(
                produceFile = {
                    androidContext().filesDir.resolve(ENV_CHANGE_STORE_KEY).absolutePath.toPath()
                }
            )
        }
        singleOf(::DeviceImplAndroid) { bind<Device>() }
        singleOf(::ApplicationInitManagerAndroid) { bind<ApplicationInitManager>() }
        singleOf(::AppDatabaseAndroidImpl) { bind<AppDatabase>() }
        singleOf(::AppLoggerAndroid) { bind<AppLogger>() }
        singleOf(::SystemHelperAndroidImpl) { bind<SystemHelper>()}
        singleOf(::PermissionHandlerAndroid) { bind<PermissionHandler>()}

        //////////////////////////////////////////
        // Login related dependencies
        //////////////////////////////////////////

        single<DataStore<Preferences>>(qualifier = named(USER_DETAILS_DATA_STORE_NAME)) {
            PreferenceDataStoreFactory.createWithPath(
                produceFile = {
                    androidContext().filesDir.resolve(USER_DETAILS_DATA_STORE_NAME).absolutePath.toPath()
                }
            )
        }

    }
}