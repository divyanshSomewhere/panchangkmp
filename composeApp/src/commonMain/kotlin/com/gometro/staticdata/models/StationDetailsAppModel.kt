package com.gometro.staticdata.models

data class StationDetailsAppModel(
    val stationId: String,
    val stationName: String,
    val stationDescription: String?,
    val location: LocationAppModel,
    val imageUrls: List<String>?
)
