package com.gometro.network.rest.request


import com.gometro.base.utils.CustomJsonParser
import com.gometro.logger.CrashlyticsLogger
import com.gometro.network.config.NetworkConstants
import kotlinx.serialization.encodeToString

/**
 * Generic network request builder. Use this builder to generate any generic network request.
 * It will also set certain default values.
 * Default values:
 * MediaType: application/json
 * httpMethod: GET
 */

class NetworkRequestBuilder(
    private val requestData: GenericRequestData,
    private val crashlyticsLogger: CrashlyticsLogger
) {
    constructor(crashlyticsLogger: CrashlyticsLogger) : this(GenericRequestData(), crashlyticsLogger)

    @Throws(IllegalArgumentException::class)
    fun addQueryParam(key: String, value: String?): NetworkRequestBuilder {
        this.addMapValue(this.requestData.queryParams, key, value)
        return this
    }

    /**
     * Set the query params if applicable.
     *
     * @param queryParams Query params to be sent in form of map of string vs string.
     * @return [NetworkRequestBuilder] for chaining
     */

    @Throws(IllegalArgumentException::class)
    fun queryParams(queryParams: Map<String, String>?): NetworkRequestBuilder {
        if (queryParams.isNullOrEmpty()) throw IllegalArgumentException("Query params cannot be empty. If you do not have any query params, " + "please do not call this function")
        this.addEntireMap(queryParams, this.requestData.queryParams)
        return this
    }

    /**
     * Set the request body. Note that, setting the body implicitly means that the request is of
     * post type. We cannot have request body for get request.
     *
     * @param requestBody RequestType object for request body.
     * @return [NetworkRequestBuilder] for chaining
     */

    inline fun <reified T_Request> body(requestBody: T_Request): NetworkRequestBuilder {
        return this.rawBody(CustomJsonParser.Json.encodeToString(requestBody))
    }

    /**
     * Set the raw request body for this request. It means that we wont be transforming this request body and send
     * it as it is to the server. Note that, until unless, its a specific use case, please continue to use
     * `body`
     *
     * @param rawRequestBody Raw request body for this request. This body will not be transformed in any form
     * and will just be sent to server.
     * @return [NetworkRequestBuilder] for chaining
     */

    fun rawBody(rawRequestBody: String): NetworkRequestBuilder {
        this.requestData.bodyJSON = rawRequestBody
        this.requestData.httpMethod = HttpRequestType.POST.httpRequestType
        return this
    }

    /**
     * Set the sub url for the request. Note that this will appended with base url.
     * This is the suggested method to set the url. In case for some reason, you would like to
     * change the base url, please use `baseUrl`. By default, it will set the base url based
     * on the current environment.
     *
     * @param subUrl Suburl to be appended with base url
     * @return [NetworkRequestBuilder] for chaining
     */

    fun subUrl(subUrl: String): NetworkRequestBuilder {
        if (subUrl.isBlank()) throw IllegalArgumentException("Sub-Url cannot be empty")

        this.requestData.subUrl = subUrl
        return this
    }

    @Throws(IllegalArgumentException::class)
    fun addPathParam(key: String, value: String?): NetworkRequestBuilder {
        this.addMapValue(this.requestData.pathParams, key, value)
        return this
    }

    /**
     * Set the path params. Path params will be used liked retrofit path params. Note that path params cannot be null, if any value is provided,
     * it might throw an IllegalArgumentException.
     *
     * @param pathParams Map of key vs path params
     * @return [NetworkRequestBuilder] for chaining
     */

    @Throws(IllegalArgumentException::class)
    fun pathParams(pathParams: Map<String, String>): NetworkRequestBuilder {
        if (pathParams == null) throw IllegalArgumentException("Path params cannot be null or empty")
        this.addEntireMap(pathParams, this.requestData.pathParams)
        return this
    }

    // Optional Parameters =============================================================================================

    @Throws(IllegalArgumentException::class)
    fun addHeader(key: String, value: String?): NetworkRequestBuilder {
        this.addMapValue(this.requestData.headers, key, value)
        return this
    }

    fun addSecureApiHeaders(): NetworkRequestBuilder {
        this.addMapValue(this.requestData.headers, NetworkConstants.Header.SECURE_API_HEADERS, NetworkConstants.Header.SECURE_API_HEADERS)
        return this
    }

    fun addXTypeHeader(): NetworkRequestBuilder {
        this.addMapValue(this.requestData.headers, NetworkConstants.Header.X_TYPE, NetworkConstants.Header.X_TYPE)
        return this
    }

    /**
     * Set the optional headers if required.
     *
     * @param headers Headers
     * @return [NetworkRequestBuilder] for chaining
     */

    @Throws(IllegalArgumentException::class)
    fun headers(headers: Map<String, String>): NetworkRequestBuilder {
        this.addEntireMap(headers, this.requestData.headers)
        return this
    }

    /**
     * Optional field to set all the timeouts (Connection, Read and Write).
     * If you want to configure the timeouts separately, use [connectTimeout], [readTimeout]
     * and [writeTimeout] respectively.
     *
     * @param timeout timeout value for all the timeouts in milliseconds
     * @return [NetworkRequestBuilder] for chaining
     */

    fun timeout(timeout: Int): NetworkRequestBuilder {
        connectTimeout(timeout)
        readTimeout(timeout)
        writeTimeout(timeout)

        return this
    }

    /**
     * Optional field to set connectTimeout
     *
     * @param connectTimeout connectTimeout value in milliseconds
     * @return [NetworkRequestBuilder] for chaining
     */

    fun connectTimeout(connectTimeout: Int): NetworkRequestBuilder {
        this.requestData.connectTimeout = connectTimeout
        return this
    }

    /**
     * Optional field to set readTimeout
     *
     * @param readTimeout readTimeout value in milliseconds
     * @return [NetworkRequestBuilder] for chaining
     */

    fun readTimeout(readTimeout: Int): NetworkRequestBuilder {
        this.requestData.readTimeout = readTimeout
        return this
    }

    /**
     * Optional field to set writeTimeout
     *
     * @param writeTimeout writeTimeout value in milliseconds
     * @return [NetworkRequestBuilder] for chaining
     */

    fun writeTimeout(writeTimeout: Int): NetworkRequestBuilder {
        this.requestData.writeTimeout = writeTimeout
        return this
    }

    /**
     * Optional field to set this request as retryable with Default strategy [RetryStrategyType.LINEAR_BACKOFF]
     * and default retryCount as 3. Set [retryCount] and [retryStrategy] to configure
     */

    fun retry(): NetworkRequestBuilder {
        return retryCount(3)
    }

    /**
     * Optional field to set number of retries for this request. Please set retryStrategy also.
     * retryCount is capped to [RetryStrategyType.maxRetryCount]. Setting a higher value will reset it to maxRetryCount
     * of your retry strategy
     *
     * @param retryCount
     * @return
     */

    fun retryCount(retryCount: Int): NetworkRequestBuilder {
        this.requestData.retryCount = retryCount
        return this
    }

    /**
     * Optional field to set retry strategy. Default is [RetryStrategyType.LINEAR_BACKOFF]
     *
     * @param retryStrategy
     * @return
     */

    fun retryStrategy(retryStrategy: RetryStrategyType): NetworkRequestBuilder {
        this.requestData.retryStrategy = retryStrategy.name
        return this
    }

    /**
     * Set the media type of this request. This is an optional parameter, default value will be set to application/json
     *
     * @param mediaType Media type of this request (Optional)
     * @return [NetworkRequestBuilder] for chaining
     */

    fun mediaType(mediaType: String): NetworkRequestBuilder {
        this.requestData.mediaType = mediaType
        return this
    }

    fun contentType(contentType: String): NetworkRequestBuilder {
        this.requestData.contentType = contentType
        return this
    }

    /**
     * Set the http request type. Please refer [HttpRequestType] for more details.
     * By default it will be treated as GET.
     *
     * @param httpRequestType Type of http request
     * @return [NetworkRequestBuilder] for chaining
     */

    fun httpMethod(httpRequestType: HttpRequestType): NetworkRequestBuilder {
        if (this.requestData.bodyJSON != null && httpRequestType == HttpRequestType.GET) {
            throw IllegalArgumentException(
                "Request body can not be set with GET method, either change the " + "method or remove the request body"
            )
        }

        this.requestData.httpMethod = httpRequestType.httpRequestType
        return this
    }

    /**
     * Set the base url of the request.
     * NOTE: Please do not set the base url until unless its for testing. In all other cases, baseUrl
     * will be decided on runtime based on the environment.
     *
     * @param baseUrl BaseUrl, in case it needs to be overridden for testing.
     * @return [NetworkRequestBuilder] for chaining
     */

    fun baseUrl(baseUrl: String): NetworkRequestBuilder {
        if (baseUrl.isBlank()) throw IllegalArgumentException("Base-Url cannot be empty")

        this.requestData.baseUrl = baseUrl
        return this
    }

    /**
     * Set the priority of the request. This priority will be used to decide the order of processing for this request.
     * Its optional and default priority is Normal.
     *
     * @param priorityLevel [PriorityLevel] priority level of the request. Please be careful with setting the
     * request priority. If this is analytics request or less important
     * request, please set the priority to low.
     * @return [NetworkRequestBuilder] for chaining
     */

    fun priority(priorityLevel: PriorityLevel): NetworkRequestBuilder {
        this.requestData.priority = priorityLevel.value
        return this
    }

    /**
     * Set [MultipartRequestFormDataType] for the actual file to upload to server
     * @param fileData
     * @return [NetworkRequestBuilder] for chaining
     */
    fun fileData(fileData: MultipartRequestFormDataType): NetworkRequestBuilder {
        if (this.requestData is MultipartRequestData) {
            this.requestData.fileData = fileData
            return this
        } else {
            throw IllegalStateException("Please use NetworkManager.getStandardNetworkRequestBuilderForMultipartRequest() for multipart request")
        }
    }

    /**
     * Set [MultipartRequestFormDataType] for additional info json
     * @param additionalInfoData
     * @return [NetworkRequestBuilder] for chaining
     */
    fun additionalInfoPart(additionalInfoData: MultipartRequestFormDataType): NetworkRequestBuilder {
        if (this.requestData is MultipartRequestData) {
            this.requestData.additionalInfoData = additionalInfoData
            return this
        } else {
            throw IllegalStateException("Please use NetworkManager.getStandardNetworkRequestBuilderForMultipartRequest() for multipart request")
        }
    }

    // =================================================================================================================
    fun build(): ChaloRequest {
        return ChaloRequest(
            genericRequestData = this.requestData,
            crashlyticsLogger = crashlyticsLogger,
        )
    }

    // Helper Methods ==========================================================================================================================================

    private fun addMapValue(map: MutableMap<String, String>, key: String, value: String?) {
        if (value == null) {
            val exception =
                IllegalArgumentException("Param value is null for key: $key, url: ${requestData.url}")
            throw exception
        }

        map[key] = value
    }

    private fun addEntireMap(
        sourceMap: Map<String, String>,
        destinationMap: MutableMap<String, String>
    ) {
        if (sourceMap.isEmpty()) throw IllegalArgumentException("Path params cannot be null or empty")

        sourceMap.entries.forEach { entry ->
            apply {
                this.addMapValue(destinationMap, entry.key, entry.value)
            }
        }
    }
}
