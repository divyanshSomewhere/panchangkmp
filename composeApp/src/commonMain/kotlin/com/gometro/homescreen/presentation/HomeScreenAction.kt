package com.gometro.homescreen.presentation

sealed interface HomeScreenAction {
    data object OnScreenOpened : HomeScreenAction

    data class OnSearchTypeSelected(val searchType: SearchType) : HomeScreenAction
    data class OnSearchQueryChange(val query: String) : HomeScreenAction
    data object OnPassengerCountIncremented: HomeScreenAction
    data object OnPassengerCountDecreased: HomeScreenAction
    data object OnProceedClicked: HomeScreenAction

    data class OnStationSelected(val stationId: String) : HomeScreenAction

    data class OnTabSelected(val index: Int) : HomeScreenAction
}

