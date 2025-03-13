package com.gometro.core.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.gometro.constants.DataStoreKeys.ENV_CHANGE_STORE_KEY
import com.gometro.core.database.AppDatabaseAndroidImpl
import com.gometro.core.init.ApplicationInitManager
import com.gometro.core.init.ApplicationInitManagerAndroid
import com.gometro.database.GometroAppDatabase
import com.gometro.logger.GometroLogger
import com.gometro.network.utils.GometroLoggerAndroid
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
        singleOf(::ApplicationInitManagerAndroid) { bind<ApplicationInitManager>() }
        singleOf(::AppDatabaseAndroidImpl) { bind<GometroAppDatabase>() }
        singleOf(::GometroLoggerAndroid) { bind<GometroLogger>() }
    }
}