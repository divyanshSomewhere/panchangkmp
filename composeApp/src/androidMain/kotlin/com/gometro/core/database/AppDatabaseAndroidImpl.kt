package com.gometro.core.database

import android.content.Context
import com.gometro.base.featurecontracts.CoroutineContextProvider
import com.gometro.database.AppDatabase

class AppDatabaseAndroidImpl(
    private val context: Context,
    coroutineContextProvider: CoroutineContextProvider
): AppDatabase(coroutineContextProvider) {
//    override fun createDatabaseDriver(): SqlDriver {
//        return AndroidSqliteDriver(
//            schema = schema,
//            context = context,
//            name = databaseName
//        )
//    }
}