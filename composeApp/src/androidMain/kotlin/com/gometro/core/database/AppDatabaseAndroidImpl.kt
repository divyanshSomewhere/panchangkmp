package com.gometro.core.database

import android.content.Context
import com.gometro.base.providers.CoroutineContextProvider
import com.gometro.database.GometroAppDatabase

class AppDatabaseAndroidImpl(
    private val context: Context,
    coroutineContextProvider: CoroutineContextProvider
): GometroAppDatabase(coroutineContextProvider) {
//    override fun createDatabaseDriver(): SqlDriver {
//        return AndroidSqliteDriver(
//            schema = schema,
//            context = context,
//            name = databaseName
//        )
//    }
}