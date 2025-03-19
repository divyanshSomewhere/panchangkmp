package com.gometro.network

import com.gometro.logger.CrashlyticsLogger
import com.gometro.network.rest.request.MultipartRequestData
import com.gometro.network.rest.request.NetworkRequestBuilder
import com.gometro.network.rest.request.PriorityLevel


class NetworkManagerImpl(
    private val chaloUrlProvider: AppUrlProvider,
    private val crashlyticsLogger: CrashlyticsLogger
) : NetworkManager {

    override fun getLowPriorityNetworkRequestBuilder(): NetworkRequestBuilder {
        return NetworkRequestBuilder(crashlyticsLogger)
            .baseUrl(chaloUrlProvider.getBaseUrl())
            .priority(PriorityLevel.PRIORITY_TYPE_LOW)
            .timeout(20000)
    }

    override fun getStandardNetworkRequestBuilder(): NetworkRequestBuilder {
        return NetworkRequestBuilder(crashlyticsLogger)
            .baseUrl(chaloUrlProvider.getBaseUrl())
            .priority(PriorityLevel.PRIORITY_TYPE_NORMAL)
            .timeout(20000)
    }

    override fun getStandardNetworkRequestBuilder(baseUrl: String): NetworkRequestBuilder {
        return NetworkRequestBuilder(crashlyticsLogger)
            .baseUrl(baseUrl = baseUrl)
            .priority(PriorityLevel.PRIORITY_TYPE_NORMAL)
            .timeout(20000)
    }

    override fun getHighPriorityNetworkRequestBuilder(): NetworkRequestBuilder {
        return NetworkRequestBuilder(crashlyticsLogger)
            .baseUrl(chaloUrlProvider.getBaseUrl())
            .priority(PriorityLevel.PRIORITY_TYPE_HIGH)
            .timeout(20000)
    }

    override fun getCustomNetworkRequestBuilder(
        priorityLevel: PriorityLevel,
        timeout: Int
    ): NetworkRequestBuilder {
        return NetworkRequestBuilder(crashlyticsLogger)
            .baseUrl(chaloUrlProvider.getBaseUrl())
            .priority(priorityLevel)
            .timeout(timeout)
    }

    override fun getStandardNetworkRequestBuilderForMultipartRequest(): NetworkRequestBuilder {
        return NetworkRequestBuilder(MultipartRequestData(), crashlyticsLogger)
            .baseUrl(chaloUrlProvider.getBaseUrl())
            .priority(PriorityLevel.PRIORITY_TYPE_NORMAL)
            .timeout(30000)
    }

    override fun getGoogleApiNetworkRequestBuilder(): NetworkRequestBuilder {
        return NetworkRequestBuilder(MultipartRequestData(), crashlyticsLogger)
            .baseUrl("https://maps.googleapis.com/maps/api")
            .priority(PriorityLevel.PRIORITY_TYPE_NORMAL)
            .timeout(30000)
    }
}
