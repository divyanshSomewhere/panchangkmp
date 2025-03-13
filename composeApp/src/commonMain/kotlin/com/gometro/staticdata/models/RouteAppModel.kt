package com.gometro.staticdata.models

data class RouteAppModel(
    val routeId: String,
    val routeName: String,
    val stationsList: List<StationDetailsAppModel>
)
