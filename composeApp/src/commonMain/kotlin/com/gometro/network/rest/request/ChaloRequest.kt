package com.gometro.network.rest.request

import com.gometro.logger.CrashlyticsLogger
import com.gometro.network.config.NetworkConstants
import com.gometro.network.exception.BaseNetworkException
import com.gometro.network.rest.AppRestClientManager
import com.gometro.network.rest.generic.ErrorType
import com.gometro.network.rest.response.NetworkResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.toMap
import kotlinx.coroutines.CancellationException

class ChaloRequest(
    private val genericRequestData: GenericRequestData,
    private val crashlyticsLogger: CrashlyticsLogger,
) {

    private val retryHandler = RequestRetryHandler(
        genericRequestData.retryCount,
        RetryStrategyType.from(genericRequestData.retryStrategy)
    )

    suspend fun processSync(): NetworkResponse {
        var isProcessing: Boolean
        var networkResponse: NetworkResponse
        do {
            networkResponse = try {
                val response = makeCall()
                processResponse(response)
            } catch (e: BaseNetworkException) {
                e.processBaseNetworkException()
            } catch (e: CancellationException) {
                NetworkResponse(
                    isSuccess = false,
                    errorType = ErrorType.TYPE_REQUEST_CANCELLED,
                    response = null,
                    headers = null,
                    httpResponseCode = 0,
                    crashlyticsLogger = crashlyticsLogger
                )
            } catch (e: Exception) {
                e.printStackTrace()
                e.stackTraceToString()
                NetworkResponse(
                    isSuccess = false,
                    errorType = ErrorType.TYPE_UNKNOWN,
                    response = null,
                    headers = null,
                    httpResponseCode = 0,
                    crashlyticsLogger = crashlyticsLogger
                )
            }

            isProcessing = networkResponse.isSuccess == false && retryHandler.shouldRetry(networkResponse) && retryHandler.readyToRetry()
        } while (isProcessing)

        return networkResponse
    }

    private suspend fun makeCall(): HttpResponse {
        // 1. Check if the request is valid, or else lets close it straight away
        if (!GenericRequestData.isValidRequest(genericRequestData)) {
            throw IllegalArgumentException("Malformed rest request")
        }

        // 2. Base Url - No request can be made without a url
        if (genericRequestData.baseUrl.isNullOrEmpty()) {
            throw IllegalArgumentException("empty base url")
        }

        val baseUrl = genericRequestData.baseUrl!!
        val priorityLevel = PriorityLevel.valueOf(genericRequestData.priority)

        // 3. Type of request - Get, Post, Delete, Put, etc?
        val httpRequestType = HttpRequestType.from(genericRequestData.httpMethod)

        // 4. Add the headers
        val headerMap = genericRequestData.headers

        // 4.b default Headers for timeout(s)
        addTimeoutHeaders(headerMap, genericRequestData)

        val chaloRestClient = AppRestClientManager.getAppRestClient(priorityLevel)

        return when(httpRequestType) {
            HttpRequestType.GET -> {
                chaloRestClient
                    .makeGetRequest(
                        url = genericRequestData.getPathTransformedFullUrl(baseUrl),
                        headerMap = headerMap,
                        queryMap = genericRequestData.queryParams
                    )
            }
            HttpRequestType.POST -> {
                when(genericRequestData) {
                    is MultipartRequestData -> {
                        chaloRestClient
                            .makeMultipartPostRequest(
                                url = genericRequestData.getPathTransformedFullUrl(baseUrl),
                                headerMap = headerMap,
                                fileItem = genericRequestData.fileData,
                                additionalInfo = genericRequestData.additionalInfoData
                            )
                    }
                    else -> {
                        chaloRestClient
                            .makePostRequest(
                                url = genericRequestData.getPathTransformedFullUrl(baseUrl),
                                headerMap = headerMap,
                                queryMap = genericRequestData.queryParams,
                                body = genericRequestData.bodyJSON
                            )
                    }
                }
            }
            HttpRequestType.PUT -> {
                chaloRestClient
                    .makePutRequest(
                        url = genericRequestData.getPathTransformedFullUrl(baseUrl),
                        headerMap = headerMap,
                        queryMap = genericRequestData.queryParams,
                        body = genericRequestData.bodyJSON
                    )
            }
            HttpRequestType.DELETE -> {
                chaloRestClient
                    .makeDeleteRequest(
                        url = genericRequestData.getPathTransformedFullUrl(baseUrl),
                        headerMap = headerMap,
                        queryMap = genericRequestData.queryParams,
                    )
            }
            HttpRequestType.UNKNOWN -> {
                throw IllegalArgumentException("Unknown type of request requested.")
            }
        }

    }

    private suspend fun processResponse(response: HttpResponse): NetworkResponse {
        return when(val httpStatusCode = response.status.value) {
            in 200 until 300 -> {
                val responseString = response.bodyAsText()
                NetworkResponse(
                    isSuccess = true,
                    errorType = ErrorType.TYPE_NONE,
                    response = responseString,
                    headers = response.headers.toMap(),
                    httpResponseCode = httpStatusCode,
                    crashlyticsLogger = crashlyticsLogger
                )
            }
            else -> {
                val errorBodyString = response.bodyAsText()
                NetworkResponse(
                    isSuccess = false,
                    errorType = ErrorType.getErrorTypeFromHttpStatusCode(httpStatusCode),
                    response = errorBodyString,
                    headers = response.headers.toMap(),
                    httpResponseCode = httpStatusCode,
                    crashlyticsLogger = crashlyticsLogger
                )
            }
        }
    }

    private fun BaseNetworkException.processBaseNetworkException(): NetworkResponse {
        return when(this) {
            is BaseNetworkException.InvalidAccessTokenUsedException -> {
                NetworkResponse(
                    isSuccess = false,
                    errorType = ErrorType.TYPE_UNAUTHORIZED,
                    response = null,
                    headers = null,
                    httpResponseCode = 0,
                    crashlyticsLogger = crashlyticsLogger
                )
            }
            is BaseNetworkException.TimeoutException -> {
                NetworkResponse(
                    isSuccess = false,
                    errorType = ErrorType.TYPE_TIMEOUT,
                    response = null,
                    headers = null,
                    httpResponseCode = 0,
                    crashlyticsLogger = crashlyticsLogger
                )
            }
            is BaseNetworkException.NetworkConnectionFailedException -> {
                NetworkResponse(
                    isSuccess = false,
                    errorType = ErrorType.TYPE_NO_INTERNET,
                    response = null,
                    headers = null,
                    httpResponseCode = 0,
                    crashlyticsLogger = crashlyticsLogger
                )
            }
        }
    }

    private fun addTimeoutHeaders(
        headerMap: MutableMap<String, String>,
        request: GenericRequestData
    ) {
        if (request.connectTimeout != null) {
            headerMap[NetworkConstants.Header.KEY_CONNECTION_TIMEOUT] =
                request.connectTimeout.toString()
        }
        if (request.readTimeout != null) {
            headerMap[NetworkConstants.Header.KEY_READ_TIMEOUT] =
                request.readTimeout.toString()
        }
        if (request.writeTimeout != null) {
            headerMap[NetworkConstants.Header.KEY_WRITE_TIMEOUT] =
                request.writeTimeout.toString()
        }
    }

}