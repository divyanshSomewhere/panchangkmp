package com.gometro.database

import com.gometro.base.providers.CoroutineContextProvider

abstract class GometroAppDatabase (
    private val coroutineContextProvider: CoroutineContextProvider
) {


    protected val databaseName: String = "app_db"
//    protected val schema get() = app_db

//    protected abstract fun createDatabaseDriver(): SqlDriver

    /*
    private val database: app by lazy { createDatabase() }

    private fun createDatabase(): app_db {
        return app_db(
            driver = createDatabaseDriver(),
//            routes_tableAdapter = RoutesTableAdapters.get(),
//            recent_route_tableAdapter = RecentRouteTableAdapter.get(),
//            recent_stop_based_trip_tableAdapter = RecentStopBasedTripTableAdapter.get(),
//            recent_place_tableAdapter = RecentPlaceTableAdapter.get(),
//            recent_stop_tableAdapter = RecentStopTableAdapter.get(),
//            recent_trip_tableAdapter = RecentTripTableAdapter.get()
        )
    }

//    val routeDao: RouteDao
//        get() = RouteDaoImpl(database.routes_tableQueries, coroutineContextProvider)
//
//    val routeRecentCityItineraryDao: RouteRecentCityItineraryDao
//        get() = RouteRecentCityItineraryDaoImpl(database.recent_route_tableQueries, coroutineContextProvider)
//
//    val recentStopBasedTripCityItineraryDao: RecentStopBasedTripCityItineraryDao
//        get() = RecentStopBasedTripCityItineraryDaoImpl(database.recent_stop_based_trip_tableQueries, coroutineContextProvider)
//
//    val recentPlacesCityItineraryDao : RecentPlacesCityItineraryDao
//        get() = RecentPlacesCityItineraryDaoImpl(database.recent_place_tableQueries, coroutineContextProvider)
//
//    val recentStopCityItineraryEntityDao: RecentStopCityItineraryEntityDao
//        get() = RecentStopCityItineraryEntityDaoImpl(database.recent_stop_tableQueries, coroutineContextProvider)
//
//    val recentTripCityItineraryDao: RecentTripCityItineraryDao
//        get() = RecentTripCityItineraryDaoImpl(database.recent_trip_tableQueries, coroutineContextProvider)
}


suspend fun <T> CoroutineContextProvider.runOnIo(action: suspend () -> T): T {
    return withContext(this.io) {
        action.invoke()
    }
}
    */
}