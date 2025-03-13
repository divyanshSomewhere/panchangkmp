package com.gometro.homescreen.presentation

import com.gometro.base.GometroMviViewModel
import com.gometro.base.providers.CoroutineContextProvider
import com.gometro.core.presentation.UiText
import com.gometro.logger.GometroLogger
import kotlinx.coroutines.delay

class HomeScreenViewModel(
    private val coroutineContextProvider: CoroutineContextProvider,
    private val gometroLogger: GometroLogger
): GometroMviViewModel<HomeScreenAction, HomeScreenDataState, HomeScreenUIState, HomeScreenSideEffect>(
) {
    init {
        initializeCoroutineScopeWhileVsCollected()
        registerForViewStateChanges()
    }

    fun onAction(action: HomeScreenAction) {
        gometroLogger.debug(tag = "HomeScreenAction", message = action.toString())
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
                gometroLogger.debug(tag = "HomeScreenAction", message =" here1")

                launchInScopeWithViewLifecycle(coroutineContext = coroutineContextProvider.main) {                gometroLogger.debug(tag = "HomeScreenAction", message =" here1")
                    gometroLogger.debug(tag = "HomeScreenAction", message =" here2")


                    delay(3000)
                    gometroLogger.debug(tag = "HomeScreenAction", message =" here3")

                    updateState {
                        it.copy(
                            testCopy = "Updated test copy"
                        )
                    }
                    gometroLogger.debug(tag = "HomeScreenAction", message =" here4")
                    gometroLogger.debug(tag = "HomeScreenAction", message =" state = "+dataState.value.testCopy)

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
        gometroLogger.debug(tag = "HomeScreenAction", message =" here 6 state = "+dataState.testCopy)

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