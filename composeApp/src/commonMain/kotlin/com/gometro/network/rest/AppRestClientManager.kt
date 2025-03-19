package com.gometro.network.rest

import com.gometro.network.rest.request.PriorityLevel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

object AppRestClientManager : KoinComponent {

    const val CLIENT_PRIORITY_LOW = "lowPriority"
    const val CLIENT_PRIORITY_MEDIUM = "mediumPriority"
    const val CLIENT_PRIORITY_HIGH = "highPriority"

    private val lowPriorityHttpClient: AppRestClient by inject<AppRestClient>(qualifier = named(CLIENT_PRIORITY_LOW))
    private val mediumPriorityHttpClient: AppRestClient by inject<AppRestClient>(qualifier = named(CLIENT_PRIORITY_MEDIUM))
    private val highPriorityHttpClient: AppRestClient by inject<AppRestClient>(qualifier = named(CLIENT_PRIORITY_HIGH))

    fun getAppRestClient(priorityLevel: PriorityLevel): AppRestClient {
        return when(priorityLevel) {
            PriorityLevel.PRIORITY_TYPE_LOW -> lowPriorityHttpClient
            PriorityLevel.PRIORITY_TYPE_NORMAL -> mediumPriorityHttpClient
            PriorityLevel.PRIORITY_TYPE_HIGH -> highPriorityHttpClient
        }
    }

}