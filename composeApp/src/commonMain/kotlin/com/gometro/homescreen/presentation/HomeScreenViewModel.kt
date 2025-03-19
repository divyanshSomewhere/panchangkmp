package com.gometro.homescreen.presentation

import com.gometro.base.BaseMviViewModel
import com.gometro.base.featurecontracts.CoroutineContextProvider
import com.gometro.core.presentation.UiText
import com.gometro.logger.AppLogger
import kotlinx.coroutines.delay

class HomeScreenViewModel(
    private val coroutineContextProvider: CoroutineContextProvider,
    private val appLogger: AppLogger
): BaseMviViewModel<HomeScreenAction, HomeScreenDataState, HomeScreenUIState, HomeScreenSideEffect>(
) {
    init {
        initializeCoroutineScopeWhileVsCollected()
        registerForViewStateChanges()
    }

    fun onAction(action: HomeScreenAction) {
        appLogger.debug(tag = "HomeScreenAction", message = action.toString())
        when(action) {
            HomeScreenAction.OnPassengerCountDecreased -> {
                handlePassengerCountDecreased()
            }
            HomeScreenAction.OnPassengerCountIncremented -> {
                handlePassengerCountIncremented()
            }
            HomeScreenAction.OnProceedClicked -> TODO()
            is HomeScreenAction.OnSearchQueryChange -> TODO()


            is HomeScreenAction.OnTabSelected -> TODO()
            is HomeScreenAction.OnSearchTypeSelected -> TODO()
            is HomeScreenAction.OnStationSelected -> TODO()
            HomeScreenAction.OnScreenOpened -> {
                appLogger.debug(tag = "HomeScreenAction", message =" here1")

                launchInScopeWithViewLifecycle(coroutineContext = coroutineContextProvider.main) {                appLogger.debug(tag = "HomeScreenAction", message =" here1")
                    appLogger.debug(tag = "HomeScreenAction", message =" here2")


                    delay(3000)
                    appLogger.debug(tag = "HomeScreenAction", message =" here3")

                    updateState {
                        it.copy(
                            testCopy = "Updated test copy"
                        )
                    }
                    appLogger.debug(tag = "HomeScreenAction", message =" here4")
                    appLogger.debug(tag = "HomeScreenAction", message =" state = "+dataState.value.testCopy)

                }
            }
        }
    }

    private fun handlePassengerCountDecreased() {
        if(dataState.value.passengerCount <= 1) {
            updateState {
                it.copy(
                    isDecreasePassengerCountBtnEnabled = false
                )
            }
            return
        }
        updateState {
            it.copy(
                passengerCount = it.passengerCount - 1
            )
        }
    }

    private fun handlePassengerCountIncremented() {
        val isDecreasePassengerCountBtnEnabled = dataState.value.passengerCount >= 1
        updateState {
            it.copy(
                passengerCount = it.passengerCount + 1,
                isDecreasePassengerCountBtnEnabled = isDecreasePassengerCountBtnEnabled
            )
        }
    }

    private fun onSearchTypeSelected(searchType: SearchType) {
        updateState {
            it.copy(
                currentSearchType = searchType
            )
        }
    }

    private fun handleSearchQueryChange(query: String) {

    }

    private fun handleTabSelected(index: Int) {

    }

    override fun initialDataState(): HomeScreenDataState {
        return HomeScreenDataState()
    }

    override suspend fun convertToUiState(dataState: HomeScreenDataState): HomeScreenUIState {
        appLogger.debug(tag = "HomeScreenAction", message =" here 6 state = "+dataState.testCopy)

        return HomeScreenUIState(
            testCopy = UiText.DynamicString(dataState.testCopy),

            fromStationString = UiText.DynamicString(dataState.selectedFromStation?.stationName?: ""),
            toStationString = UiText.DynamicString(dataState.selectedToStation?.stationName?: ""),
            passengerCount = UiText.DynamicString(dataState.passengerCount.toString()),
            farePerTicket = null,
            totalFare = null,
            isProceedBtnEnabled = false
        )
    }
}