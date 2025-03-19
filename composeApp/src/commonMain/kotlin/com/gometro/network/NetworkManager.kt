package com.gometro.network

import com.gometro.network.rest.request.NetworkRequestBuilder
import com.gometro.network.rest.request.PriorityLevel


interface NetworkManager {
    fun getLowPriorityNetworkRequestBuilder(): NetworkRequestBuilder
    fun getStandardNetworkRequestBuilder(): NetworkRequestBuilder
    fun getHighPriorityNetworkRequestBuilder(): NetworkRequestBuilder
    fun getStandardNetworkRequestBuilder(baseUrl: String): NetworkRequestBuilder
    fun getCustomNetworkRequestBuilder(
        priorityLevel: PriorityLevel,
        timeout: Int
    ): NetworkRequestBuilder
    fun getStandardNetworkRequestBuilderForMultipartRequest(): NetworkRequestBuilder
    fun getGoogleApiNetworkRequestBuilder(): NetworkRequestBuilder
}
