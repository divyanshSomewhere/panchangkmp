package com.gometro.homescreen.presentation

import com.gometro.core.presentation.UiText
import com.gometro.staticdata.models.StationDetailsAppModel

data class HomeScreenDataState(

    val testCopy: String = "Initial test copy",

    val availableMetroStationsList: List<StationDetailsAppModel>? = null,

    val currentSearchType: SearchType = SearchType.FROM_STATION_SEARCH,

    val fromStopSearchString: String? = null,
    val toStopSearchString: String? = null,

    val selectedFromStation: StationDetailsAppModel? = null,
    val selectedToStation: StationDetailsAppModel? = null,
    val passengerCount: Int = 0,

    val isDecreasePassengerCountBtnEnabled: Boolean = false,
    val isIncreasePassengerCountBtnEnabled: Boolean = false,

    val isProceedBtnEnabled: Boolean = false
)

data class HomeScreenUIState(
    val testCopy: UiText,

    val fromStationString: UiText?,
    val toStationString: UiText?,
    val passengerCount: UiText,

    val farePerTicket: UiText?,
    val totalFare: UiText?,

    val isProceedBtnEnabled: Boolean
)

enum class SearchType {
    FROM_STATION_SEARCH,
    TO_STATION_SEARCH,
    ROUTE_SEARCH
}


