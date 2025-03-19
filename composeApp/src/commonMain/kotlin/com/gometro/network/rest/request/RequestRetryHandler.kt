package com.gometro.network.rest.request

import com.gometro.network.rest.response.NetworkResponse
import kotlinx.coroutines.delay
import kotlin.math.pow

class RequestRetryHandler(var maxRetries: Int, private val retryStrategyType: RetryStrategyType) {

    init {
        maxRetries = minOf(maxRetries, retryStrategyType.maxRetryCount)
    }

    companion object {
        private const val CONSTANT_BACKOFF_IN_MILLISECONDS: Long = 1000
        private const val POLYNOMIAL: Double = 2.0
    }

    var retryCount = 0

    fun retryWithConstantBackoff(): Long = CONSTANT_BACKOFF_IN_MILLISECONDS

    fun retryWithLinearBackoff(): Long = retryCount * 1000.toLong()

    fun retryWithExponentialBackoff(): Long = (2.0.pow(retryCount.toDouble())).toLong() * 1000

    fun retryWithPolynomialBackoff(): Long = (retryCount.toDouble().pow(POLYNOMIAL)).toLong() * 1000

    fun shouldRetry(networkResponse: NetworkResponse): Boolean {
        return retryCount < maxRetries && retryStrategyType != RetryStrategyType.UNKNOWN && isRetriable(
            networkResponse.httpResponseCode
        )
    }

    private fun isRetriable(httpResponseCode: Int): Boolean {
        return httpResponseCode / 100 == 5 // for 5xx errors
    }

    suspend fun readyToRetry(): Boolean {
        val sleepTime = when (retryStrategyType) {
            RetryStrategyType.NO_BACKOFF -> 0
            RetryStrategyType.LINEAR_BACKOFF -> retryWithLinearBackoff()
            RetryStrategyType.EXPONENTIAL_BACKOFF -> retryWithExponentialBackoff()
            RetryStrategyType.CONSTANT_BACKOFF -> retryWithConstantBackoff()
            RetryStrategyType.POLYNOMIAL_BACKOFF -> retryWithPolynomialBackoff()
            RetryStrategyType.UNKNOWN -> throw IllegalStateException("There must be a retry strategy")
        }
        retryCount++
        delay(sleepTime)
        return true
    }
}
