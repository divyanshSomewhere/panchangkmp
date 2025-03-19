package com.gometro.base.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

object UtilityFunctions {

    /**
     * @param timeInMillis total time for which timer should run
     * @param tickerTimeInMillis delay in between each emission
     * @return a flow which emits remaining time after every tick
     */
    fun getCountdownTimerFlow(timeInMillis: Long, tickerTimeInMillis: Long = 1000): Flow<Long> {
        return ((timeInMillis - tickerTimeInMillis) downTo 0 step tickerTimeInMillis).asFlow()
            .onEach { delay(tickerTimeInMillis) }
            .onStart { emit(timeInMillis) }
    }
}
